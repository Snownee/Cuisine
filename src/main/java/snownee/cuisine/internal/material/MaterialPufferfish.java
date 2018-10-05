package snownee.cuisine.internal.material;

import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.MaterialCategory;

public class MaterialPufferfish extends MaterialWithEffect
{

    public MaterialPufferfish(String id, int rawColor, int cookedColor, int waterValue, int oilValue, int heatValue, float foodSaturationModifier, MaterialCategory... categories)
    {
        super(id, CulinaryHub.CommonEffects.ALWAYS_EDIBLE, rawColor, cookedColor, waterValue, oilValue, heatValue, foodSaturationModifier, categories);
    }

    @Override
    public void onCrafted(Ingredient ingredient)
    {
        ingredient.addEffect(CulinaryHub.CommonEffects.PUFFERFISH_POISON);
    }

    @Override
    public void onCooked(CompositeFood.Builder<?> dish, Ingredient ingredient, CookingVessel vessel, EffectCollector collector)
    {
        super.onCooked(dish, ingredient, vessel, collector);
        ingredient.addEffect(CulinaryHub.CommonEffects.WATER_BREATHING);
    }

}
