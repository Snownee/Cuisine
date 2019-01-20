package snownee.cuisine.api.process;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.BasinInteracting.Output;

public interface Boiling extends CuisineProcessingRecipe
{
    @Override
    default boolean matches(Object... inputs)
    {
        if (inputs == null || inputs.length != 3 || inputs[0].getClass() != ItemStack.class || inputs[1] == null || inputs[1].getClass() != FluidStack.class || inputs[2].getClass() != Integer.class)
        {
            return false;
        }
        return matches((ItemStack) inputs[0], (FluidStack) inputs[1], (int) inputs[2]);
    }

    boolean matches(ItemStack item, @Nonnull FluidStack fluid, int heatValue);

    Output getOutputAndConsumeInput(ItemStack item, @Nonnull FluidStack fluid, int heatValue, Random rand);
}
