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
    public MaterialRice(String id)
    {
        super(id, -4671304, 0, 1, 1, 2, 2F, MaterialCategory.GRAIN);
    }

    @Override
    public void onCooked(CompositeFood.Builder<?> dish, Ingredient ingredient, CookingVessel vessel, EffectCollector collector)
    {
        collector.addEffect(DefaultTypes.USE_DURATION_MODIFIER, -0.75F);
    }
}
