package snownee.cuisine.api.process.prefab;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.kiwi.crafting.input.ProcessingInput;

public class SimpleSqueezing implements BasinInteracting
{
    private final ProcessingInput input;
    private final FluidStack outputFluid;
    private final ItemStack outputItem;

    public SimpleSqueezing(ProcessingInput input, FluidStack outputFluid)
    {
        this(input, outputFluid, ItemStack.EMPTY);
    }

    public SimpleSqueezing(ProcessingInput input, FluidStack outputFluid, ItemStack outputItem)
    {
        this.input = input;
        this.outputFluid = outputFluid;
        this.outputItem = outputItem;
    }

    @Override
    public boolean matches(ItemStack item, @Nullable FluidStack fluid)
    {
        if (fluid == null || outputFluid.equals(fluid))
        {
            if (input.matches(item))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Output getOutput(ItemStack item, @Nullable FluidStack fluid)
    {
        int amount = fluid == null ? 0 : fluid.amount;
        FluidStack copy = outputFluid.copy();
        copy.amount += amount;
        return new Output(fluid, outputItem);
    }

    // TODO: JEI support @3TUSK
    //
    //    @Override
    //    public List<ItemStack> getInputItems()
    //    {
    //        return input.examples();
    //    }
    //
    //    @Override
    //    public List<FluidStack> getInputFluids()
    //    {
    //        return Lists.newArrayList();
    //    }
    //
    //    @Override
    //    public List<FluidStack> getOutputs()
    //    {
    //        return Arrays.asList(output);
    //    }

}
