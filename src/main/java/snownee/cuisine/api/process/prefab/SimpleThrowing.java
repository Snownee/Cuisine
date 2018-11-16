package snownee.cuisine.api.process.prefab;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.AbstractCuisineProcessingRecipe;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.kiwi.crafting.input.ProcessingInput;

public class SimpleThrowing extends AbstractCuisineProcessingRecipe implements BasinInteracting
{
    private final ProcessingInput inputItem;
    private final FluidStack inputFluid;
    private final ItemStack outputItem;

    public SimpleThrowing(ResourceLocation identifier, ProcessingInput inputItem, @Nonnull FluidStack inputFluid, ItemStack outputItem)
    {
        super(identifier);
        this.inputItem = inputItem;
        this.inputFluid = inputFluid;
        this.outputItem = outputItem;
    }

    @Override
    public boolean matches(ItemStack item, FluidStack fluid)
    {
        return fluid != null && fluid.containsFluid(inputFluid) && matchesItem(item);
    }

    @Override
    public boolean matchesItem(ItemStack item)
    {
        return inputItem.matches(item);
    }

    @Override
    public Output getOutput(ItemStack item, FluidStack fluid, Random rand)
    {
        FluidStack copy = new FluidStack(fluid, fluid.amount - inputFluid.amount);
        return new Output(copy, outputItem.copy());
    }

    @Override
    public void consumeInput(ItemStack item, FluidStack fluid, Random rand)
    {
        item.shrink(inputItem.count());
    }

    public ProcessingInput getInputItem()
    {
        return inputItem;
    }

    public FluidStack getInputFluid()
    {
        return inputFluid;
    }

    public ItemStack getOutputItem()
    {
        return outputItem;
    }

}
