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

    @Override
    public void beginCook(CompositeFood.Builder dish)
    {
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
    public void postCook(CompositeFood.Builder dish, CookingVessel vessel)
    {

    }

    @Override
    public void endCook()
    {
        Cuisine.logger.debug("End of inspection");
    }

}
