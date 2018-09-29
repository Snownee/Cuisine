package snownee.cuisine.api;

final class NoOperationCookingStrategy implements CookingStrategy
{

    /**
     * Holder of original CompositeFood object.
     */
    private CompositeFood dish;

    NoOperationCookingStrategy()
    {
        // No-op, used for restricting access level
    }

    @Override
    public void beginCook(CompositeFood dish)
    {
        this.dish = dish;
    }

    @Override
    public void preCook(Seasoning seasoning, CookingVessel vessel)
    {
        // No-op
    }

    @Override
    public void cook(Ingredient ingredient, CookingVessel vessel)
    {
        // No-op
    }

    @Override
    public void postCook(CookingVessel vessel)
    {
        // No-op
    }

    @Override
    public void endCook()
    {
        // No-op
    }

    @Override
    public CompositeFood result()
    {
        return this.dish;
    }
}
