package snownee.cuisine.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
     * properties of ingredient objects.
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
     * Snownee:
     * 1. Size 是所有材料 Size 之和向上取整得来的，它决定了一道菜食用一次可提供的饱食度。一
     * 个食材 ItemStack 参考 Size 是 1，也就是可提供半个🍗。一般情况下一道菜的 Size 不能超过
     * 8，也就是 4🍗
     *
     * 2. Durability 是一道菜可被食用的次数，默认为6次（待定），会受到食材特性和烹饪技巧的
     * 影响。（3TUSK: 这个现在叫 Serves，大约是“例”的意思，想想下馆子的时候怎么点菜的就知道了）
     */

    @Deprecated // TODO Merge into CompositeFood.Builder#addIngredient, as a pre-check
    public boolean canAdd(final Ingredient ingredient)
    {
        return this.getSize() + ingredient.getSize() <= this.getMaxSize() /*&& ingredient.getMaterial().canAddInto(this, ingredient)*/;
    }

    public CompositeFood addEffect(final Effect effect)
    {
        if (!contains(effect))
        {
            this.effects.add(effect);
        }
        return this;
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
                saturationModifier += ingredient.getSaturationModifier(); // TODO: relate to size
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

    /**
     * @deprecated Recipe type will be immutable in the future; custom name support
     *             will be provided for custom dish name display; custom model
     *             support will be part of recipe system
     * @param type Model type name
     */
    // TODO Migrate to incoming Recipe system
    @Deprecated
    public abstract void setModelType(String type);

    public void removeEffect(Effect effect) // TODO?
    {
        this.effects.removeIf(e -> e == effect);
    }

    /**
     * A {@code Builder} represents an unfinished {@link CompositeFood} object.
     *
     * @param <F> The concrete resultant type of {@link CompositeFood} that this
     *            {@code Builder} may produce.
     */
    @SuppressWarnings({"unused", "WeakerAccess"}) // TODO (3TUSK): Remove these when we are ready
    public static abstract class Builder<F extends CompositeFood>
    {

        private List<Ingredient> ingredients;
        private List<Seasoning> seasonings;

        /**
         * Construct a {@code Builder} instance using default implementations.
         *
         * @implSpec
         * By default, this constructor uses {@link ArrayList} for all places where
         * it needs {@link List}.
         */
        protected Builder()
        {
            this(new ArrayList<>(), new ArrayList<>());
        }

        /**
         * Construct a {@code Builder} instance using an existed {@link CompositeFood}
         * instance. This constructor mimics the real-life situation of re-cook food.
         *
         * @implSpec
         * By default, this constructor directly re-use the reference of {@link
         * CompositeFood#ingredients} and {@link CompositeFood#seasonings}.
         *
         * @param finishedDish An instance of finished food object.
         */
        protected Builder(CompositeFood finishedDish)
        {
            this(finishedDish.ingredients, finishedDish.seasonings);
        }

        /**
         * Construct a {@code Builder} instance using explicit {@link List}. May be used
         * for special situations, e.g. insertion order needs to be preserved, and thus
         * a {@link LinkedList} is required.
         *
         * @param ingredients list of ingredients
         * @param seasonings list of seasonings
         */
        protected Builder(List<Ingredient> ingredients, List<Seasoning> seasonings)
        {
            this.ingredients = ingredients;
            this.seasonings = seasonings;
        }

        /**
         * Returns a {@link Class} object that represents the concrete type of wrapped
         * {@link CompositeFood} object in return value of {@link #build()}.
         *
         * @implSpec
         * Return value of this method must satisfy that:
         *
         * <pre>
         *     Optional&lt;CompositeFood&gt; result = this.build();
         *     if (result.isPresent())
         *     {
         *         assert getType().isInstance(result.get());
         *     }
         * </pre>
         *
         * @return A class object that represents the food type this can produce
         */
        public abstract Class<F> getType();

        /**
         * Retrieve the list of current {@link Ingredient ingredients} present.
         * @return A list of ingredients
         */
        public List<Ingredient> getIngredients()
        {
            return ingredients;
        }

        /**
         * Retrieve the list of current {@link Seasoning seasonings} present.
         * @return A list of seasonings
         */
        public List<Seasoning> getSeasonings()
        {
            return seasonings;
        }

        public double getCurrentSize()
        {
            return this.ingredients.stream().mapToDouble(Ingredient::getSize).sum();
        }

        public double getMaxSize()
        {
            return CompositeFood.DEFAULT_MAX_SIZE;
        }

        // mutating operations

        public boolean canAddIntoThis(EntityPlayer cook, Ingredient ingredient, CookingVessel vessel)
        {
            return ingredient.getMaterial().canAddInto(this, ingredient);
        }

        public boolean canAddIntoThis(EntityPlayer cook, Seasoning seasoning, CookingVessel vessel)
        {
            return true;
        }

        /**
         * Attempt to add an instance of {@link Ingredient} into this; it will be
         * merged if there is already the same type of {@link Ingredient} present.
         * The attempt may fail due to a certain reason which is left for {@link
         * #canAddIntoThis(EntityPlayer, Ingredient, CookingVessel) actual
         * implementation} to specify.
         *
         * @param cook the entity that conduct this attempt (i.e. "cook")
         * @param ingredient new ingredient to be added
         * @param vessel present cooking vessel
         *
         * @return true if the ingredient given is added or merged; false otherwise.
         */
        public final boolean addIngredient(EntityPlayer cook, Ingredient ingredient, CookingVessel vessel)
        {
            if (this.canAddIntoThis(cook, ingredient, vessel))
            {
                boolean merged = false;
                for (Ingredient i : ingredients)
                {
                    if (i.equalsIgnoreSize(ingredient))
                    {
                        i.increaseSizeBy(ingredient.getSize());
                        merged = true;
                        break;
                    }
                }
                if (!merged)
                {
                    ingredients.add(ingredient);
                }
                ingredient.getMaterial().onAddedInto(this, ingredient, vessel);
                return true;
            }
            else
            {
                return false;
            }
        }

        /**
         * Attempt to add an instance of {@link Seasoning} into this; it will be
         * merged if there is already the same type of {@link Seasoning} present.
         * The attempt may fail due to a certain reason which is left for {@link
         * #canAddIntoThis(EntityPlayer, Seasoning, CookingVessel) actual
         * implementation} to specify.
         *
         * @param cook the entity that conduct this attempt (i.e. "cook")
         * @param seasoning new seasoning to be added
         * @param vessel present cooking vessel
         *
         * @return true if the seasoning given is added or merged; false otherwise.
         */
        public final boolean addSeasoning(EntityPlayer cook, Seasoning seasoning, CookingVessel vessel)
        {
            if (this.canAddIntoThis(cook, seasoning, vessel))
            {
                boolean merged = false;
                for (Seasoning s : seasonings)
                {
                    if (seasoning.matchType(seasoning))
                    {
                        seasoning.merge(seasoning);
                        merged = true;
                        break;
                    }
                }
                if (!merged)
                {
                    seasonings.add(seasoning);
                }
                seasoning.getSpice().onAddedInto(this, vessel);
                return true;
            }
            else
            {
                return false;
            }
        }

        public final boolean removeIngredient(Ingredient ingredient)
        {
            boolean changed = false;
            for (Iterator<Ingredient> itr = this.ingredients.iterator(); itr.hasNext();)
            {
                Ingredient i = itr.next();
                if (ingredient.equalsIgnoreSize(i))
                {
                    i.decreaseSizeBy(ingredient.getSize());
                    if (i.getSize() <= 0)
                    {
                        // i.getMaterial().onRemovedFrom(this, i, cookingVessel???); // TODO (3TUSK): callback
                        itr.remove();
                    }
                    changed = true;
                    break;
                }
            }
            return changed;
        }

        public final boolean removeSeasoning(Seasoning seasoning)
        {
            boolean changed = false;
            for (Iterator<Seasoning> itr = this.seasonings.iterator(); itr.hasNext();)
            {
                Seasoning s = itr.next();
                if (s.matchType(seasoning))
                {
                    s.decreaseSizeBy(seasoning.getSize());
                    if (s.getSize() <= 0)
                    {
                        // s.getSpice().onRemovedFrom(this, i, cookingVessel???); // TODO (3TUSK): callback
                        itr.remove();
                    }
                    changed = true;
                    break;
                }
            }
            return changed;
        }

        /**
         * Process this {@code CompositeFood.Builder} object using given {@link CookingStrategy}.
         *
         * @param strategy The {@code CookingStrategy} instance that will "visit" this
         * @param vessel The container where this is contained within
         */
        public final void apply(final CookingStrategy strategy, final CookingVessel vessel)
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
            strategy.postCook(this, vessel);
            strategy.endCook();
        }

        /**
         * Construct the {@link CompositeFood} instance which represents a finished
         * dish object.
         * <p>
         * Note that the result is wrapped in {@link Optional}; this implies that the
         * build process may fail and thus return an {@link Optional#empty() empty
         * Optional instance}. Presence check, such as {@link Optional#isPresent()} or
         * {@link Optional#ifPresent} shall be performed in order to get correct
         * behaviors. This method may be called multiple times for situations where
         * a non-empty return value is desired, or multiple copies of {@link F food
         * objects} are needed.
         * </p>
         * Note that this may be an expensive call under certain situations.
         *
         * @return An Optional instance that may hold the resultant CompositeFood
         *         instance; or an empty Optional instance if the build process failed.
         */
        public abstract Optional<F> build();
    }

}
