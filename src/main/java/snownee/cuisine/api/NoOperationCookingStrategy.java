package snownee.cuisine.api;

final class NoOperationCookingStrategy implements CookingStrategy
{
    static final NoOperationCookingStrategy INSTANCE = new NoOperationCookingStrategy();

    private NoOperationCookingStrategy()
    {
        // No-op, used for restricting access level
    }

    @Override
    public void beginCook(CompositeFood.Builder dish)
    {
        // No-op
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
    public void postCook(CompositeFood.Builder dish, CookingVessel vessel)
    {
        // No-op
    }

    @Override
    public void endCook()
    {
        // No-op
    }

}
