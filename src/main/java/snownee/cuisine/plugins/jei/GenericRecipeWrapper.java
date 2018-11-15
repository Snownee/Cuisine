package snownee.cuisine.plugins.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import snownee.cuisine.api.process.CuisineProcessingRecipe;

public abstract class GenericRecipeWrapper<R extends CuisineProcessingRecipe> implements IRecipeWrapper
{
    final R recipe;

    public GenericRecipeWrapper(R recipe)
    {
        this.recipe = recipe;
    }
}
