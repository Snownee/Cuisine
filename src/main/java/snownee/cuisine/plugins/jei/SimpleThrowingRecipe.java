package snownee.cuisine.plugins.jei;

import java.util.Collections;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import snownee.cuisine.api.process.prefab.SimpleThrowing;

public class SimpleThrowingRecipe implements IRecipeWrapper
{
    final SimpleThrowing recipe;

    public SimpleThrowingRecipe(SimpleThrowing recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        JEICompat.arrowIn.draw(minecraft, 23, 11);
        JEICompat.arrowInOverlay.draw(minecraft, 23, 11);
        JEICompat.arrowOut.draw(minecraft, 60, 11);
        JEICompat.arrowOutOverlay.draw(minecraft, 60, 11);
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(recipe.getInputItem().examples()));
        ingredients.setInput(VanillaTypes.FLUID, recipe.getInputFluid());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutputItem());
    }

}
