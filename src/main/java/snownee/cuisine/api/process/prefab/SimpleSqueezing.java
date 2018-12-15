package snownee.cuisine.api.process.prefab;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.AbstractCuisineProcessingRecipe;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.kiwi.crafting.input.ProcessingInput;

public class SimpleSqueezing extends AbstractCuisineProcessingRecipe implements BasinInteracting
{
    private final ProcessingInput input;
    private final FluidStack outputFluid;
    private final ItemStack outputItem;

    public SimpleSqueezing(ResourceLocation identifier, ProcessingInput input, FluidStack outputFluid)
    {
        this(identifier,input, outputFluid, ItemStack.EMPTY);
    }

    public SimpleSqueezing(ResourceLocation identifier, ProcessingInput input, FluidStack outputFluid, ItemStack outputItem)
    {
        super(identifier);
        this.input = input;
        this.outputFluid = outputFluid;
        this.outputItem = outputItem;
    }

    @Override
    public boolean matches(ItemStack item, @Nullable FluidStack fluid)
    {
        if (fluid == null || outputFluid.equals(fluid))
        {
            return matchesItem(item);
        }
        return false;
    }

    @Override
    public boolean matchesItem(ItemStack item)
    {
        return input.matches(item);
    }

    @Override
    public Output getOutput(ItemStack item, @Nullable FluidStack fluid, Random rand)
    {
        int amount = fluid == null ? 0 : fluid.amount;
        FluidStack copy = outputFluid.copy();
        copy.amount += amount;
        return new Output(copy, outputItem.copy());
    }

    @Override
    public void consumeInput(ItemStack item, FluidStack fluid, Random rand)
    {
        item.shrink(input.count());
    }

    public ProcessingInput getInputItem()
    {
        return input;
    }

    public FluidStack getOutputFluid()
    {
        return outputFluid;
    }

    public ItemStack getOutputItem()
    {
        return outputItem;
    }

}
