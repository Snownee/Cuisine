package snownee.cuisine.api.process.prefab;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.kiwi.crafting.input.ProcessingInput;

public class SimpleSqueezing implements BasinInteracting
{
    private final ProcessingInput input;
    private final FluidStack output;

    public SimpleSqueezing(ProcessingInput input, FluidStack output)
    {
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(ItemStack item, @Nullable FluidStack fluid)
    {
        if (fluid == null || output.equals(fluid))
        {
            if (input.matches(item))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public FluidStack getOutput(ItemStack item, @Nullable FluidStack fluid)
    {
        int amount = fluid == null ? 0 : fluid.amount;
        FluidStack copy = output.copy();
        copy.amount += amount;
        return copy;
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
