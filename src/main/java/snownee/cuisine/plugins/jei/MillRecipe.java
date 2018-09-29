package snownee.cuisine.plugins.jei;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import snownee.cuisine.api.process.Milling;

public class MillRecipe implements IRecipeWrapper
{

    final Milling recipe;

    public MillRecipe(Milling recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(recipe.getInput().examples()));
        if (recipe.getInputFluid() != null)
        {
            ingredients.setInput(VanillaTypes.FLUID, recipe.getInputFluid());
        }
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
        if (recipe.getOutputFluid() != null)
        {
            ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
        }
    }

}
