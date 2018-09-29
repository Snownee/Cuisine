package snownee.cuisine.internal.material;

import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;

public class MaterialApple extends SimpleMaterialImpl
{

    public MaterialApple(String id, int rawColor, int cookedColor, int waterValue, int oilValue, int heatValue, float saturationModifier, MaterialCategory... categories)
    {
        super(id, rawColor, cookedColor, waterValue, oilValue, heatValue, saturationModifier, categories);
    }

    @Override
    public void onCooked(CompositeFood dish, Ingredient ingredient, CookingVessel vessel, final EffectCollector collector)
    {
        ingredient.addEffect(CulinaryHub.CommonEffects.EXPERIENCED);
    }
}
