package snownee.cuisine.plugins.jei;

import java.util.Arrays;
import java.util.Collections;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import snownee.cuisine.api.process.Chopping;

public class ChoppingBoardAxeRecipe implements IRecipeWrapper
{
    private final Chopping recipe;

    ChoppingBoardAxeRecipe(Chopping recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(recipe.input.examples(), JEICompat.AXES));
        ingredients.setOutputs(VanillaTypes.ITEM, Collections.singletonList(recipe.getOutput()));
    }

}
