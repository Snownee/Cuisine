package snownee.cuisine.library;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public final class FluidHandlerIOWrapper implements IFluidHandler
{

    private final IFluidHandler input, output;

    public FluidHandlerIOWrapper(IFluidHandler input, IFluidHandler output)
    {
        this.input = input;
        this.output = output;
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return new IFluidTankProperties[] { input.getTankProperties()[0], output.getTankProperties()[0] };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        return input.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        return output.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return output.drain(maxDrain, doDrain);
    }
}
