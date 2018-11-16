package snownee.cuisine.api.process.prefab;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.AbstractCuisineProcessingRecipe;
import snownee.cuisine.api.process.BasinInteracting.Output;
import snownee.cuisine.api.process.Boiling;

public class DistillationBoiling extends AbstractCuisineProcessingRecipe implements Boiling
{
    private final FluidStack input;
    private final ItemStack output;
    private final int heatValue;

    public DistillationBoiling(FluidStack input, ItemStack output)
    {
        this(input, output, 1);
    }

    public DistillationBoiling(FluidStack input, ItemStack output, int heatValue)
    {
        super(new ResourceLocation("cuisine", "distilling_" + input.getFluid().getName()));
        this.input = input;
        this.output = output;
        this.heatValue = heatValue;
    }

    @Override
    public boolean matches(ItemStack item, @Nonnull FluidStack fluid, int heatValue)
    {
        return heatValue >= this.heatValue && fluid.containsFluid(input);
    }

    @Override
    public Output getOutputAndConsumeInput(ItemStack item, @Nonnull FluidStack fluid, int heatValue)
    {
        FluidStack outputFluid = fluid.copy();
        outputFluid.amount -= input.amount;
        return new Output(outputFluid, output.copy());
    }

    public FluidStack getInput()
    {
        return input;
    }

    public ItemStack getOutput()
    {
        return output;
    }

    public int getMinimumHeat()
    {
        return heatValue;
    }

}
