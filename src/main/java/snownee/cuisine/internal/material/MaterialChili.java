package snownee.cuisine.internal.material;

import java.util.EnumSet;

import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;

public class MaterialChili extends SimpleMaterialImpl
{

    public MaterialChili(String id)
    {
        super(id, -2878173, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES);
        setValidForms(EnumSet.of(Form.CUBED, Form.SHREDDED, Form.MINCED));
    }

    @Override
    public void onCooked(CompositeFood.Builder<?> dish, Ingredient ingredient, CookingVessel vessel, EffectCollector collector)
    {
        if (dish.contains(CulinaryHub.CommonMaterials.SICHUAN_PEPPER) || dish.contains(CulinaryHub.CommonSpices.SICHUAN_PEPPER_POWDER))
        {
            dish.addEffect(CulinaryHub.CommonEffects.HOT);
        }
    }

}
