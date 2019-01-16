package snownee.cuisine.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import snownee.cuisine.api.CulinaryHub.CommonEffects;
import snownee.cuisine.api.prefab.DefaultConsumedCollector;

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
     * The fixed saturation modifier of this dish.
     */
    private final float saturationModifier;

    /**
     * The fixed food level of this dish.
     */
    private final int foodLevel;

    private float useDurationModifier = 1F;

    /**
     * The overall quality bonus of this Dish, initial to 1.
     */
    private double qualityBonus = 1.0;

    /**
     * The number of sevres of this composite.
     */
    private int durability;

    private final int maxServeSize;

    /**
     * Construct a CompositeFood instance from given lists of ingredients, of seasonings
     * and of effects.
     *
     * @param ingredients The initial ingredients
     * @param seasonings The initial seasonings
     * @param effects The initial effects
     */
    protected CompositeFood(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects)
    {
        this(ingredients, seasonings, effects, 0, 0F);
    }

    public CompositeFood(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects, int hungerRestore, float saturation)
    {
        this(ingredients, seasonings, effects, hungerRestore, saturation, DEFAULT_SERVE_AMOUNT);
    }

    protected CompositeFood(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects, int hungerRestore, float saturation, int serve_amount)
    {
        this.ingredients = ingredients;
        this.seasonings = seasonings;
        this.effects = effects;
        this.maxServeSize = (this.durability = serve_amount);
        this.foodLevel = hungerRestore;
        this.saturationModifier = saturation;
    }

    /**
     * Returns a unique identifier that can distinguish this type of {@code CompositeFood}
     * from other types of {@code CompositeFood}. Used for situations like serialization.
     *
     * @return a unique identifier
     */
    public abstract ResourceLocation getIdentifier();

    /**
     * Returns a collection of "keywords" that describes the basic characteristics of
     * this type of {@code CompositeFood}. For example, if a concrete type of {@code
     * CompositeFood} represents salad, the returned collection may contain string
     * {@code salad}. East-Asian cuisine may choose to include string {@code east-asian}.
     * Food that contains rice, corn, wheat, potato as majority may want to have
     * {@code staple-food} in its returned collection.
     * <p>
     * A keyword entry must be matched with regular expression <code>[\w\-]+</code>.
     * </p>
     *
     * @return a read-only collection of "keywords"
     */
    public abstract Collection<String> getKeywords();

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

    public final Set<Effect> getMergedEffects()
    {
        Set<Effect> effects = getIngredients().stream().map(Ingredient::getEffects).flatMap(Set::stream).collect(Collectors.toSet());
        effects.addAll(getEffects());
        return effects;
    }

    /**
     * Determine whether this is consumed entirely.
     * @return <code>true</code> if this is already eaten up; <code>false</code> otherwise.
     */
    public final boolean isEmpty()
    {
        return this.durability < 1 || this.maxServeSize < 1 || this.ingredients.isEmpty();
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

    /**
     * Return the saturation modifier that this can provide.
     * @return Saturation modifier
     */
    public float getSaturationModifier()
    {
        return saturationModifier;
    }

    /**
     * Return the food level, i.e. the hunger bar regeneration amount, that this can provide.
     * @return Food level
     */
    public int getFoodLevel()
    {
        return foodLevel;
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

    public void onEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
        Collection<EffectBinding> bindings = getEffectBindings();
        DefaultConsumedCollector collector = new DefaultConsumedCollector(getFoodLevel());

        // And then apply them
        for (EffectBinding binding : bindings)
        {
            binding.effect.onEaten(stack, player, this, binding.ingredients, collector);
        }

        // And finally, consume seasonings
        for (Seasoning seasoning : seasonings)
        {
            seasoning.getSpice().onConsumed(stack, player, worldIn, seasoning, collector);
        }

        collector.apply(this, player);

        int countOvercooked = (int) getIngredients().stream().filter(i -> i.getAllTraits().contains(IngredientTrait.OVERCOOKED)).count();
        int newFoodLevel = collector.getNewFoodLevel() - countOvercooked;
        if (newFoodLevel > 0)
        {
            player.getFoodStats().addStats(newFoodLevel, getSaturationModifier());
        }
    }

    protected Collection<EffectBinding> getEffectBindings()
    {
        Multimap<Effect, Ingredient> effectMap = HashMultimap.create();
        for (Ingredient ingredient : ingredients)
        {
            ingredient.getEffects().forEach(effect -> effectMap.put(effect, ingredient));
        }
        effects.forEach(effect -> effectMap.put(effect, null));

        List<EffectBinding> bindings = new ArrayList<>();
        effectMap.keySet().forEach(effect -> bindings.add(new EffectBinding(effectMap.get(effect).stream().collect(Collectors.toList()), effect)));

        // Sort the list of effects based on priority
        Collections.sort(bindings);

        return bindings;
    }

    public static class EffectBinding implements Comparable<EffectBinding>
    {
        public final Effect effect;
        public final List<Ingredient> ingredients; // element can be null

        public EffectBinding(List<Ingredient> ingredients, @Nonnull Effect effect)
        {
            this.ingredients = ingredients;
            this.effect = effect;
        }

        @Override
        public int compareTo(@Nonnull EffectBinding another)
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

    /**
     * A {@code Builder} represents an unfinished {@link CompositeFood} object.
     *
     * @param <F> The concrete resultant type of {@link CompositeFood} that this
     *            {@code Builder} may produce.
     */
    public static abstract class Builder<F extends CompositeFood>
    {

        private List<Ingredient> ingredients;
        private List<Seasoning> seasonings;
        private List<Effect> effects;

        /**
         * Construct a {@code Builder} instance using default implementations.
         *
         * @implSpec
         * By default, this constructor uses {@link ArrayList} for all places where
         * it needs {@link List}.
         */
        protected Builder()
        {
            this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
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
            this(finishedDish.ingredients, finishedDish.seasonings, finishedDish.effects);
        }

        /**
         * Construct a {@code Builder} instance using explicit {@link List}. May be used
         * for special situations, e.g. insertion order needs to be preserved, and thus
         * a {@link LinkedList} is required.
         *
         * @param ingredients list of ingredients
         * @param seasonings list of seasonings
         */
        protected Builder(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects)
        {
            this.ingredients = ingredients;
            this.seasonings = seasonings;
            this.effects = effects;
        }

        /**
         * Returns a {@link Class} object that represents the concrete type of wrapped
         * {@link CompositeFood} object in return value of {@link #build}.
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
         * All implementations should guarantee that the returned list is
         * mutable.
         * @return A list of ingredients
         */
        public List<Ingredient> getIngredients()
        {
            return ingredients;
        }

        /**
         * Retrieve the list of current {@link Seasoning seasonings} present.
         * All implementations should guarantee that the returned list is
         * mutable.
         * @return A list of seasonings
         */
        public List<Seasoning> getSeasonings()
        {
            return seasonings;
        }

        /**
         * Retrieve the list of current {@link Effect effects} present.
         * All implementations should guarantee that the returned list is
         * mutable.
         * @return A list of effects
         */
        public List<Effect> getEffects()
        {
            return effects;
        }

        /**
         * Determine whether this contains an instance of {@link Ingredient ingredient}
         * with specified {@link Material}.
         *
         * @param mat The material in question
         * @return true if there is at least one instance of Ingredient has given
         *         Material; false otherwise.
         */
        public boolean contains(Material mat)
        {
            for (Ingredient ingredient : this.ingredients)
            {
                if (ingredient.getMaterial() == mat)
                {
                    return true;
                }
            }
            return false;
        }

        /**
         * Determine whether this contains an instance of {@link Seasoning seasoning}
         * with specified {@link Spice}.
         *
         * @param spice The spice in question
         * @return true if there is at least one instance of Seasoning has given
         *         Spice; false otherwise.
         */
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

        public int getMaxIngredientLimit()
        {
            return 6;
        }

        // mutating operations

        public boolean canAddIntoThis(EntityPlayer cook, Ingredient ingredient, CookingVessel vessel)
        {
            return ingredients.size() >= getMaxIngredientLimit() || ingredient.getMaterial().canAddInto(this, ingredient);
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
        public boolean addIngredient(EntityPlayer cook, Ingredient ingredient, CookingVessel vessel)
        {
            if (this.canAddIntoThis(cook, ingredient, vessel))
            {
                if (ingredients.size() >= getMaxIngredientLimit())
                {
                    return false;
                }
                ingredients.add(ingredient);

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
        public boolean addSeasoning(EntityPlayer cook, Seasoning seasoning, CookingVessel vessel)
        {
            if (this.canAddIntoThis(cook, seasoning, vessel))
            {
                boolean merged = false;
                for (Seasoning s : seasonings)
                {
                    if (s.matchType(seasoning))
                    {
                        s.merge(seasoning);
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

        /**
         * Attempt to add an {@link Effect} into this.
         *
         * @param e new effect to be added
         */
        public void addEffect(Effect e)
        {
            this.effects.add(e);
        }

        public boolean removeIngredient(Ingredient ingredient)
        {
            return ingredients.remove(ingredient);
        }

        public boolean removeSeasoning(Seasoning seasoning)
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
                        s.getSpice().onRemovedFrom(this);
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
         * @param cook the entity that conducts this cooking
         * @param vessel the cooking vessel used
         *
         * @return An Optional instance that may hold the resultant CompositeFood
         *         instance; or an empty Optional instance if the build process failed.
         */
        public abstract Optional<F> build(final CookingVessel vessel, EntityPlayer cook);

    }

}
