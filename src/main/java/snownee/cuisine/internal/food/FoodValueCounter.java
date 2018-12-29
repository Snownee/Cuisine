package snownee.cuisine.internal.food;

import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingStrategy;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.Seasoning;

/**
 * A pseudo-cooking-strategy that does not manipulate {@link Ingredient ingredients}
 * nor {@link Seasoning seasonings} at all, but calculates the total food values
 * (including hunger regeneration and saturation value).
 */
public class FoodValueCounter implements CookingStrategy
{
    private float hungerRegen, saturation;

    public FoodValueCounter()
    {
        this(0, 0F);
    }

    public FoodValueCounter(float initialHungerRegen, float initialSaturation)
    {
        this.hungerRegen = initialHungerRegen;
        this.saturation = initialSaturation;
    }

    @Override
    public void beginCook(CompositeFood.Builder dish)
    {

    }

    @Override
    public void preCook(Seasoning seasoning, CookingVessel vessel)
    {

    }

    @Override
    public void cook(Ingredient ingredient, CookingVessel vessel)
    {
        // Base saturation * natural logarithm of ingredient size. (Commented for now)
        // So that the bonus will be less significant as the size grows up.
        this.saturation += ingredient.getSaturationModifier()/* * Math.max(Math.log(ingredient.getSize()), 1.0)*/;
        if (ingredient.hasTrait(IngredientTrait.PLAIN) || ingredient.hasTrait(IngredientTrait.OVERCOOKED))
        {
            this.saturation -= 0.1;
        }
        this.hungerRegen += ingredient.getFoodLevel() * (ingredient.hasTrait(IngredientTrait.PLAIN) ? 0.5 : 1);
    }

    @Override
    public void postCook(CompositeFood.Builder dish, CookingVessel vessel)
    {

    }

    @Override
    public void endCook()
    {

    }

    public int getHungerRegen()
    {
        return (int) Math.ceil(this.hungerRegen);
    }

    public float getSaturation()
    {
        return Math.max(this.saturation, 0);
    }
}
