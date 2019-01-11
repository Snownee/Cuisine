package snownee.cuisine.internal.material;

import java.util.Calendar;

import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;

public class MaterialPumpkin extends SimpleMaterialImpl
{

    public MaterialPumpkin(String id)
    {
        super(id, -663885, 0, 1, 1, 1, 0F, MaterialCategory.VEGETABLES);
        setValidForms(Form.ALL_FORMS_INCLUDING_JUICE);
    }

    @Override
    public void onCooked(CompositeFood.Builder<?> dish, Ingredient ingredient, CookingVessel vessel, final EffectCollector collector)
    {
        if (!ingredient.hasTrait(IngredientTrait.UNDERCOOKED) && !ingredient.hasTrait(IngredientTrait.OVERCOOKED) && !ingredient.hasTrait(IngredientTrait.PLAIN))
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 5);
            if (calendar.get(Calendar.MONTH) != Calendar.NOVEMBER)
            {
                return;
            }
            calendar.add(Calendar.DAY_OF_YEAR, -10);
            if (calendar.get(Calendar.MONTH) != Calendar.OCTOBER)
            {
                return;
            }
            ingredient.addEffect(CulinaryHub.CommonEffects.SPOOKY);
        }
    }
}
