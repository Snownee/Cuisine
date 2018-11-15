package snownee.cuisine.plugins.jei;

import java.util.Collections;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import snownee.cuisine.api.process.prefab.SimpleSqueezing;

public class SimpleSqueezingRecipe extends GenericRecipeWrapper<SimpleSqueezing>
{
    public SimpleSqueezingRecipe(SimpleSqueezing recipe)
    {
        super(recipe);
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(recipe.getInputItem().examples()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutputItem());
        ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
    }
}
