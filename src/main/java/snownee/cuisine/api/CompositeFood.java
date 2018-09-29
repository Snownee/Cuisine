package snownee.cuisine.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import snownee.cuisine.api.CulinaryHub.CommonEffects;
import snownee.cuisine.api.prefab.DefaultConsumedCollector;
import snownee.cuisine.api.prefab.DefaultCookedCollector;

/**
 * A CompositeFood object represents data of an edible {@link ItemStack ItemStack}.
 * Normally it is held by a {@link FoodContainer} instance as Capability.
 */
public abstract class CompositeFood
{

    /**
     * The default amount of serves of any CompositeFood instance, which is
     * number of eight (8). This means that this CompositeFood can be eaten
     * eight (8) times.
     */
    private static final int DEFAULT_SERVE_AMOUNT = 8;

    /**
     * The default maximum amount of each serves, which determines food amount
     * (vanilla). It also describes how many ingredients are used in this food,
     * functioning as an indicator of quantity of this {@code CompositeFood}.
     */
    private static final double DEFAULT_MAX_SIZE = 8.0;

    /**
     * List of instanised edible material. By using List, it enables some illogical
     * dish like "tomato fries tomato" (A Chinese joke, where "tomato" have two
     * different names)
     */
    protected final List<Ingredient> ingredients;

    /**
     * Set of instanised spices. Spices are modifiers to materials, they will affect
     * properties of ingredient objects. The implementation of {@link #addSeasoning}
     * will handle duplicated seasoning by merging them together.
     */
    protected final List<Seasoning> seasonings;

    /**
     * List of special effects that will apply on players when eaten.
     */
    protected final List<Effect> effects;

    /**
     * The saturation modifier of this dish.
     */
    private float saturationModifier;

    /**
     * The food level of this dish.
     */
    private int foodLevel;

    private float useDurationModifier = 1F;

    /**
     * A signal used for lazy evaluation of foodLevel and saturationModifier. Never
     * be serialized.
     */
    private transient boolean requireFoodStateRefresh = true;

    /**
     * The overall quality bonus of this Dish, initial to 1.
     */
    private double qualityBonus = 1.0;

    /**
     * The durability of this composite.
     */
    private int durability;

    private final int maxServeSize;

    /**
     * Construct an empty CompositeFood instance.
     */
    public CompositeFood()
    {
        this(new ArrayList<>(8));
    }

    /**
     * Construct a CompositeFood instance from an array of {@link Ingredient ingredients}.
     * @param ingredients The initial ingredients
     */
    public CompositeFood(Ingredient... ingredients)
    {
        this(new ArrayList<>(Arrays.asList(ingredients)));
    }

    /**
     * Construct a CompositeFood instance from a list of {@link Ingredient ingredients}.
     * @param ingredients The initial ingredients
     */
    public CompositeFood(List<Ingredient> ingredients)
    {
        this(ingredients, new ArrayList<>(8), new ArrayList<>(4));

    }

    /**
     * Construct a CompositeFood instance from given lists of ingredients, of seasonings
     * and of effects.
     *
     * @param ingredients The initial ingredients
     * @param seasonings The initial seasonings
     * @param effects The initial effects
     */
    public CompositeFood(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects)
    {
        this.ingredients = ingredients;
        this.seasonings = seasonings;
        this.effects = effects;
        this.maxServeSize = (this.durability = DEFAULT_SERVE_AMOUNT);
    }

    /**
     * Retrieve a read-only view of a list of all ingredients at the moment this is called.
     * @return A read-only view of all current present ingredients
     */
    public final List<Ingredient> getIngredients()
    {
        return Collections.unmodifiableList(this.ingredients);
    }

    /**
     * Retrieve a read-only view of a list of all seasonings at the moment this is called.
     * @return A read-only view of all current present seasonings
     */
    public final List<Seasoning> getSeasonings()
    {
        return Collections.unmodifiableList(this.seasonings);
    }

    /**
     * Retrieve a read-only view of a list of all effects at the moment this is called.
     * @return A read-only view of all current present effects
     */
    public final List<Effect> getEffects()
    {
        return Collections.unmodifiableList(this.effects);
    }

    /**
     * Determine whether this is consumed entirely.
     * @return <code>true</code> if this is already eaten up; <code>false</code> otherwise.
     */
    public final boolean isEmpty()
    {
        return this.durability < 1 || this.maxServeSize < 1;
    }

    /*
     * Implementation Summary
     *
     * Yes, this an attempt on utilizing visitor pattern, where CookingStrategy is
     * visitor, which will visits each component of a CompositeFood.
     *
     * The invocations of these "accept" methods ("cook" for CookingStrategy,
     * "addFlavorTo" for Seasoning) represent the process of modifying (i.e.
     * cooking) the food in this composite. As such, one can simply define general
     * logic for cooking strategies in order to modify the CompositeFood object in
     * different ways. Example:
     *
     * 1. Define new Seasoning class "SoySauce". Let variable "sauce" be instance of
     * SoySauce, and `compositeFood' is instance of CompositeFood. Invoking
     * compositeFood.flavorWith(sauce) will be equivalent to sauce.addFlavorTo(compositeFood),
     * where all elements in compositeFood.ingredients will be modified (becoming
     * both tasty but salty, depending on second parameter of Spice::addFlavorTo;
     * also the render will also be affected, i.e. darker).
     *
     * 2. Define new Seasoning object "MineralWater". Let variable `water' be
     * instance of MineralWater, and `compositeFood' is instance of CompositeFood.
     * Similarly, invoking compositeFood.flavorWith(sauce) will be equivalent to
     * sauce.addFlavorTo(compositeFood), however this time there is basically nothing
     * happened, due to implementation.
     *
     * 3. Define new CookingStrategy class `RapidFrying'. Let variable `frying' be
     * instance of RapidFrying, and `compositeFood' is instance of CompositeFood.
     * Invoking compositeFood.apply(frying) will cause frying object iterating through
     * all elements in `ingredients', and "enchanting tastiness respectively" (i.e.
     * increment on several fields).
     */

    /*
     * Snownee:
     * 1. Size 是所有材料 Size 之和向上取整得来的，它决定了一道菜食用一次可提供的饱食度。一
     * 个食材 ItemStack 参考 Size 是 1，也就是可提供半个🍗。一般情况下一道菜的 Size 不能超过
     * 8，也就是 4🍗
     *
     * 2. Durability 是一道菜可被食用的次数，默认为6次（待定），会受到食材特性和烹饪技巧的
     * 影响。（3TUSK: 这个现在叫 Serves，大约是“例”的意思，想想下馆子的时候怎么点菜的就知道了）
     */

    /**
     * Process this {@code CompositeFood} object using given {@link CookingStrategy}.
     *
     * @param strategy The {@code CookingStrategy} instance that will "visit" this
     * @param vessel The container where this is contained within
     *
     * @return The resultant {@code CompositeFood} after processing finished.
     */
    public final CompositeFood apply(final CookingStrategy strategy, final CookingVessel vessel)
    {
        strategy.beginCook(this);
        for (Seasoning seasoning : this.seasonings)
        {
            strategy.preCook(seasoning, vessel);
        }
        for (Ingredient ingredient : this.ingredients)
        {
            strategy.cook(ingredient, vessel);
        }
        strategy.postCook(vessel);
        strategy.endCook();

        requireFoodStateRefresh = true;
        return strategy.result();
    }

    public CompositeFood flavorWith(final Seasoning seasoning, final CookingVessel vessel)
    {
        seasoning.addFlavorTo(this);
        if (addSeasoning(seasoning))
        {
            seasoning.getSpice().onAddedInto(this, vessel);
        }
        requireFoodStateRefresh = true;
        return this;
    }

    public boolean canAdd(final Ingredient ingredient)
    {
        return this.getSize() + ingredient.getSize() <= this.getMaxSize() && ingredient.getMaterial().canAddInto(this, ingredient);
    }

    public CompositeFood addIngredient(final Ingredient ingredient)
    {
        requireFoodStateRefresh = true;
        for (Ingredient i : ingredients)
        {
            if (i.equalsIgnoreSize(ingredient))
            {
                i.increaseSizeBy(ingredient.getSize());
                return this;
            }
        }
        ingredients.add(ingredient);
        return this;
    }

    public CompositeFood addEffect(final Effect effect)
    {
        if (!contains(effect))
        {
            this.effects.add(effect);
        }
        return this;
    }

    private boolean addSeasoning(final Seasoning newSeasoning)
    {
        for (Seasoning seasoning : seasonings)
        {
            if (seasoning.matchType(newSeasoning))
            {
                seasoning.merge(newSeasoning);
                return false;
            }
        }
        seasonings.add(newSeasoning);
        requireFoodStateRefresh = true;
        return true;
    }

    /**
     * @param material The material to lookup
     * @return true if there is at least one Ingredient whose Material is equals to
     * the one given; false otherwise
     */
    public boolean contains(Material material)
    {
        // An O(n) implementation by its nature
        for (Ingredient ingredient : this.ingredients)
        {
            if (ingredient.getMaterial() == material)
            {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Spice spice)
    {
        for (Seasoning seasoning : this.seasonings)
        {
            if (seasoning.getSpice() == spice)
            {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Effect effect)
    {
        return effects.contains(effect) || ingredients.stream().anyMatch(i -> i.getEffects().contains(effect));
    }

    public float getSaturationModifier()
    {
        refreshState();
        return saturationModifier;
    }

    public int getFoodLevel()
    {
        refreshState();
        return foodLevel;
    }

    private void refreshState()
    {
        if (requireFoodStateRefresh)
        {
            requireFoodStateRefresh = false;

            saturationModifier = 0.4F;
            for (Ingredient ingredient : ingredients)
            {
                saturationModifier += ingredient.getSaturationModifier(); // TODO: ralate to size
                if (ingredient.hasTrait(IngredientTrait.PLAIN) || ingredient.hasTrait(IngredientTrait.OVERCOOKED))
                {
                    saturationModifier -= 0.1;
                }
            }
            saturationModifier = Math.max(saturationModifier, 0);

            float i = 0;
            for (Ingredient ingredient : ingredients)
            {
                i += ingredient.getFoodLevel() * (ingredient.hasTrait(IngredientTrait.PLAIN) ? 0.5 : 1);
            }
            foodLevel = MathHelper.ceil(i);
        }
    }

    public void setQualityBonus(double qualityBonus)
    {
        this.qualityBonus = qualityBonus;
    }

    public double getQualityBonus()
    {
        return qualityBonus;
    }

    public boolean alwaysEdible()
    {
        return contains(CommonEffects.ALWAYS_EDIBLE);
    }

    public double getSize()
    {
        return this.ingredients.stream().mapToDouble(Ingredient::getSize).sum();
    }

    public double getMaxSize()
    {
        return DEFAULT_MAX_SIZE;
    }

    /**
     * Get the amount of serves, which describes how many times this can be eaten.
     * @return Current amount of serves left
     */
    public int getServes()
    {
        return durability;
    }

    /**
     * Set the amount of serves, which describes how many times this can be eaten.
     * @param durability The new amount of serves
     */
    public void setServes(int durability)
    {
        this.durability = durability;
    }

    /**
     *
     * @return The maximum amount of time that this can be eaten.
     */
    public int getMaxServes()
    {
        return maxServeSize;
    }

    public float getUseDurationModifier()
    {
        return useDurationModifier;
    }

    public void setUseDurationModifier(float useDurationModifier)
    {
        this.useDurationModifier = useDurationModifier;
    }

    public boolean isHavingSideEffect()
    {
        return !effects.isEmpty();
    }

    /**
     * Called right before this is about to be put into a {@link FoodContainer}.
     *
     * 在装入 {@link FoodContainer} 之前会调用此方法。
     *
     * @param vessel The {@link CookingVessel} that makes this.
     * @param playerIn The {@link EntityPlayer} that conducts this action
     */
    // 这是之前的 endCook，改现在这个名字是因为 serve 还有盛菜的意思
    public void onBeingServed(final CookingVessel vessel, EntityPlayer playerIn)
    {
        EffectCollector collector = new DefaultCookedCollector();

        int seasoningSize = 0;
        int waterSize = 0;
        for (Seasoning seasoning : this.getSeasonings())
        {
            Spice spice = seasoning.getSpice();
            spice.onCooked(this, seasoning, vessel, collector);
            if (spice == CulinaryHub.CommonSpices.WATER)
            {
                waterSize += seasoning.getSize();
            }
            else if (spice != CulinaryHub.CommonSpices.EDIBLE_OIL && spice != CulinaryHub.CommonSpices.SESAME_OIL)
            {
                seasoningSize += seasoning.getSize();
            }
        }
        boolean isPlain = seasoningSize == 0 || (this.getSize() / seasoningSize) / (1 + waterSize / 3) > 3;

        for (Ingredient ingredient : this.ingredients)
        {
            Material material = ingredient.getMaterial();
            Set<MaterialCategory> categories = material.getCategories();
            if (isPlain && !categories.contains(MaterialCategory.SEAFOOD) && !categories.contains(MaterialCategory.FRUIT))
            {
                ingredient.addTrait(IngredientTrait.PLAIN);
            }
            material.onCooked(this, ingredient, vessel, collector);
        }

        collector.apply(this, playerIn);
    }

    public void onEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
        Collection<IngredientBinding> bindings = getEffectBindings();
        EffectCollector collector = new DefaultConsumedCollector();

        // And then apply them
        for (IngredientBinding binding : bindings)
        {
            binding.effect.onEaten(stack, player, this, binding.ingredient, collector);
        }

        // And finally, consume seasonings
        for (Seasoning seasoning : seasonings)
        {
            seasoning.getSpice().onConsumed(stack, player, worldIn, seasoning, collector);
        }

        collector.apply(this, player);
    }

    protected Collection<IngredientBinding> getEffectBindings()
    {
        List<IngredientBinding> bindings = new ArrayList<>();

        effects.forEach(effect -> bindings.add(new IngredientBinding(effect)));
        for (Ingredient ingredient : ingredients)
        {
            for (Effect effect : ingredient.getEffects())
            {
                bindings.add(new IngredientBinding(ingredient, effect));
            }
        }

        // Sort the list of effects based on priority
        Collections.sort(bindings);

        return bindings;
    }

    public static class IngredientBinding implements Comparable<IngredientBinding>
    {
        public final Ingredient ingredient;
        public final Effect effect;

        public IngredientBinding(@Nullable Ingredient ingredient, @Nonnull Effect effect)
        {
            this.ingredient = ingredient;
            this.effect = effect;
        }

        public IngredientBinding(@Nonnull Effect effect)
        {
            this(null, effect);
        }

        @Override
        public int compareTo(@Nonnull IngredientBinding another)
        {
            return Integer.compare(another.effect.getPriority(), this.effect.getPriority());
        }
    }

    // TODO Require redesign: do we really need this when we are now capability?
    public ItemStack makeItemStack()
    {
        ItemStack dishItem = getBaseItem();
        FoodContainer container = dishItem.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container == null)
        {
            throw new NullPointerException("Target ItemStack does not supports FoodContainer capability");
        }
        else
        {
            container.set(this);
        }
        return dishItem;
    }

    // TODO Require redesign
    public abstract ItemStack getBaseItem();

    // TODO Migrate to incoming Recipe system
    public abstract String getOrComputeModelType();

    // TODO Migrate to incoming Recipe system
    public abstract void setModelType(String type);

    public void removeEffect(Effect effect) // TODO?
    {
        this.effects.removeIf(e -> e == effect);
    }

    // TODO A big question: CompositeFood.Builder? For those "unfinished food".
    // TODO If CompositeFood.Builder is a thing, you should be able to get one from CompositeFood, i.e. re-cook food

}
