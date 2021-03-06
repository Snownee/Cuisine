package snownee.cuisine.internal.material;

import snownee.cuisine.api.CompositeFood.Builder;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;

public class MaterialTomato extends SimpleMaterialImpl
{

    public MaterialTomato(String id)
    {
        super(id, -2681308, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES);
        setValidForms(Form.ALL_FORMS_INCLUDING_JUICE);
    }

    @Override
    public void onMade(Builder<?> dish, Ingredient ingredient, CookingVessel vessel, EffectCollector collector)
    {
        if (ingredient.hasTrait(IngredientTrait.PLAIN) || ingredient.hasTrait(IngredientTrait.OVERCOOKED))
        {
            return;
        }
        if (dish.getSeasonings().stream().anyMatch(e -> e.hasKeyword("sugar")))
        {
            dish.addEffect(CulinaryHub.CommonEffects.SUSTAINED_RELEASE);
        }
    }

}
