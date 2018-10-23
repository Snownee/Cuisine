package snownee.cuisine.plugins.jei;

import java.util.Collections;
import java.util.EnumSet;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.process.prefab.MaterialSqueezing;
import snownee.cuisine.fluids.FluidJuice;
import snownee.cuisine.items.ItemIngredient;

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
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(ItemIngredient.getAllValidFormsWithException(recipe.getMaterial(), EnumSet.of(Form.FULL, Form.PASTE))));
        ingredients.setOutput(VanillaTypes.FLUID, FluidJuice.make(recipe.getMaterial(), 250));
    }

}
