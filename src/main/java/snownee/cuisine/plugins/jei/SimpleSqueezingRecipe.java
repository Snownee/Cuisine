package snownee.cuisine.plugins.jei;

import java.util.Collections;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import snownee.cuisine.api.process.prefab.SimpleSqueezing;

public class SimpleSqueezingRecipe implements IRecipeWrapper
{
    final SimpleSqueezing recipe;

    public SimpleSqueezingRecipe(SimpleSqueezing recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(recipe.getInputItem().examples()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutputItem());
        ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
    }
}
