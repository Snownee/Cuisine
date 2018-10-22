package snownee.cuisine.plugins.jei;

import mezz.jei.api.ingredients.IIngredients;
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
        // TODO Auto-generated method stub

    }
}
