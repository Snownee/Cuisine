package snownee.cuisine.api;

/**
 * A {@code CookingStrategy} defines a set of procedures to manipulating the food
 * based on the food itself and its container.
 */
public interface CookingStrategy
{

    /**
     * Begin the cooking procedure
     * @param dish The incoming {@code CompositeFood} object at its initial
     *             state
     */
    void beginCook(final CompositeFood.Builder<?> dish);

    /**
     * Manipulate each {@code Seasoning} objects.
     *
     * @throws IllegalStateException when this method is called before
     *                               {@link #beginCook} is called, or
     *                               after this has been already called
     */
    void preCook(final Seasoning seasoning, final CookingVessel vessel);

    /**
     * Manipulate each {@code Ingredient} objects.
     * 
     * @param ingredient
     *            The ingredient object to be manipulated.
     * @param vessel
     *            The cooking vessel where ingredient is in, may not be null.
     *
     * @throws IllegalStateException when this method is called before
     *                               {@link #preCook} is called, or
     *                               after this has been already called
     */
    void cook(final Ingredient ingredient, final CookingVessel vessel);

    /**
     *
     *
     * @throws IllegalStateException when this method is called before
     *                               {@link #cook} is called, or after
     *                               this has been already called
     */
    void postCook(final CompositeFood.Builder<?> dish, final CookingVessel vessel);

    /**
     * Finish the cooking by executing all necessary procedures left.
     *
     * @throws IllegalStateException when this method is called before
     *                               {@link #postCook} is called, or
     *                               after this has been already called
     */
    void endCook();

    /**
     * Create an instance of {@code CookingStrategy} that does nothing.
     */
    static CookingStrategy identity()
    {
        return NoOperationCookingStrategy.INSTANCE;
    }
}
