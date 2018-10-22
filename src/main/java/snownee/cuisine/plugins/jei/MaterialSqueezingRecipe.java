package snownee.cuisine.plugins.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import snownee.cuisine.api.process.prefab.MaterialSqueezing;

public class MaterialSqueezingRecipe implements IRecipeWrapper
{
    final MaterialSqueezing recipe;

    public MaterialSqueezingRecipe(MaterialSqueezing recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        // TODO Auto-generated method stub

    }

}
