package snownee.cuisine.internal.material;

import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;

public class MaterialChorusFruit extends SimpleMaterialImpl
{

    public MaterialChorusFruit(String id)
    {
        super(id, -6271615, 0, 1, 1, 1, -0.1F, MaterialCategory.FRUIT, MaterialCategory.SUPERNATURAL);
        setValidForms(Form.ALL_FORMS_INCLUDING_JUICE);
    }

    @Override
    public void onCooked(CompositeFood.Builder<?> dish, Ingredient ingredient, CookingVessel vessel, final EffectCollector collector)
    {
        ingredient.addEffect(CulinaryHub.CommonEffects.TELEPORT);
    }

}
