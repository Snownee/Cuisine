package snownee.cuisine.internal.material;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import snownee.cuisine.api.CompositeFood.Builder;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.MaterialCategory;

public class MaterialTofu extends MaterialWithEffect
{

    public MaterialTofu(String id)
    {
        super(id, CulinaryHub.CommonEffects.HARMONY, -2311026, 0, 1, 1, 1, 0.4F, MaterialCategory.PROTEIN, MaterialCategory.GRAIN);
        setValidForms(EnumSet.of(Form.CUBED, Form.SLICED, Form.DICED, Form.MINCED));
    }

    @Override
    public void onCooked(Builder<?> dish, Ingredient ingredient, CookingVessel vessel, EffectCollector collector)
    {
        removeBadTraits(ingredient);
        for (Ingredient i : dish.getIngredients())
        {
            if (i == ingredient)
            {
                continue;
            }
            if (removeBadTraits(i))
            {
                return;
            }
        }
    }

    private boolean removeBadTraits(Ingredient ingredient)
    {
        List<IngredientTrait> traits = ingredient.getAllTraits().stream().filter(IngredientTrait::isBad).collect(Collectors.toList());
        for (IngredientTrait trait : traits)
        {
            System.out.println(trait);
            ingredient.removeTrait(trait);
        }
        return !traits.isEmpty();
    }

}
