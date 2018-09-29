package snownee.cuisine.internal.material;

import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.DefaultTypes;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;

public class MaterialRice extends SimpleMaterialImpl
{
    public MaterialRice(String id, int rawColor, int cookedColor, int waterValue, int oilValue, int heatValue, float saturationModifier, MaterialCategory... categories)
    {
        super(id, rawColor, cookedColor, waterValue, oilValue, heatValue, saturationModifier, categories);
    }

    @Override
    public void onCooked(CompositeFood dish, Ingredient ingredient, CookingVessel vessel, EffectCollector collector)
    {
        collector.addEffect(DefaultTypes.USE_DURATION_MODIFIER, -0.75F);
    }
}
