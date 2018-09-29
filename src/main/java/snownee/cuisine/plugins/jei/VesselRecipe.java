package snownee.cuisine.plugins.jei;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.Vessel;

public class VesselRecipe implements IRecipeWrapper
{
    final Vessel recipe;

    public VesselRecipe(Vessel recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInput(VanillaTypes.FLUID, new FluidStack(recipe.getSolvent(), 100));
        FluidStack fluidOutput = recipe.getOutputFluid();
        if (fluidOutput != null)
        {
            ingredients.setOutput(VanillaTypes.FLUID, fluidOutput);
        }
        ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(recipe.getInput().examples(), recipe.getExtraRequirement().examples()));
        ingredients.setOutputLists(VanillaTypes.ITEM, ImmutableList.of(recipe.getOutput().examples()));
    }

}
