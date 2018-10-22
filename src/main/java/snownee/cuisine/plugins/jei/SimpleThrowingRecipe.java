package snownee.cuisine.plugins.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import snownee.cuisine.api.process.prefab.SimpleThrowing;

public class SimpleThrowingRecipe implements IRecipeWrapper
{
    final SimpleThrowing recipe;

    public SimpleThrowingRecipe(SimpleThrowing recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        // TODO Auto-generated method stub

    }

}
