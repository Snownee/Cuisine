package snownee.cuisine.api.process.prefab;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.kiwi.crafting.input.ProcessingInput;

public class SimpleSqueezing implements BasinInteracting
{
    private final ProcessingInput input;
    private final FluidStack outputFluid;
    private final ItemStack outputItem;
    private final ResourceLocation identifier;

    public SimpleSqueezing(ProcessingInput input, FluidStack outputFluid)
    {
        this(input, outputFluid, ItemStack.EMPTY);
    }

    public SimpleSqueezing(ProcessingInput input, FluidStack outputFluid, ItemStack outputItem)
    {
        this.input = input;
        this.outputFluid = outputFluid;
        this.outputItem = outputItem;
        identifier = new ResourceLocation("cuisine", input.examples().get(0).getItem().getRegistryName().getPath() + "_to_" + outputItem.getItem().getRegistryName().getPath());
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

    @Override
    public ResourceLocation getIdentifier()
    {
        return identifier;
    }

}
