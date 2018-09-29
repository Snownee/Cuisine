package snownee.cuisine.api.prefab;

import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingStrategy;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Seasoning;

/**
 * An example implementation of {@code CookingStrategy} that does nothing
 * besides logging every components out.
 */
public class DebuggingCookingStrategy implements CookingStrategy
{

    /**
     * Holder of original {@code CompositeFood} object.
     */
    private CompositeFood dish;

    @Override
    public void beginCook(CompositeFood dish)
    {
        this.dish = dish;
        Cuisine.logger.debug("Inspecting CompositeFood@{}", System.identityHashCode(dish));
    }

    @Override
    public void preCook(Seasoning seasoning, CookingVessel vessel)
    {
        Cuisine.logger.debug("  - Seasoning: {}", seasoning);
    }

    @Override
    public void cook(Ingredient ingredient, CookingVessel vessel)
    {
        Cuisine.logger.debug("  - Ingredient: {}", ingredient);
    }

    @Override
    public void postCook(CookingVessel vessel)
    {

    }

    @Override
    public void endCook()
    {

    }

    @Override
    public CompositeFood result()
    {
        return this.dish;
    }
}
