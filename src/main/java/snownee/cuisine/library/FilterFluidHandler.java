package snownee.cuisine.library;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * A wrapper of {@link IFluidHandler} that filters input {@link FluidStack}.
 */
public class FilterFluidHandler implements IFluidHandler
{
    private final IFluidHandler parent;
    private final Predicate<FluidStack> validator;

    public FilterFluidHandler(IFluidHandler parent, Predicate<FluidStack> validator)
    {
        this.parent = parent;
        this.validator = validator;
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        IFluidTankProperties[] properties = parent.getTankProperties();
        for (int i = 0; i < properties.length; i++)
        {
            // TODO (3TUSK): I am not exactly sure what's going on, but FluidTank somehow manages to cache
            //   the wrapped version of IFluidTankProperties. This is the workaround.
            if (!(properties[i] instanceof FilterFluidTankProperties))
            {
                properties[i] = new FilterFluidTankProperties(properties[i]);
            }
        }
        return properties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (validator.test(resource))
        {
            return parent.fill(resource, doFill);
        }
        else
        {
            return 0;
        }
    }

    @Override
    @Nullable
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        return parent.drain(resource, doDrain);
    }

    @Override
    @Nullable
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return parent.drain(maxDrain, doDrain);
    }

    private final class FilterFluidTankProperties implements IFluidTankProperties
    {
        private final IFluidTankProperties parent;

        private FilterFluidTankProperties(IFluidTankProperties parent)
        {
            this.parent = parent;
        }

        @Override
        @Nullable
        public FluidStack getContents()
        {
            return parent.getContents();
        }

        @Override
        public int getCapacity()
        {
            return parent.getCapacity();
        }

        @Override
        public boolean canFill()
        {
            return parent.canFill();
        }

        @Override
        public boolean canDrain()
        {
            return parent.canDrain();
        }

        @Override
        public boolean canFillFluidType(FluidStack fluidStack)
        {
            return FilterFluidHandler.this.validator.test(fluidStack) && parent.canFillFluidType(fluidStack);
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack)
        {
            return parent.canDrainFluidType(fluidStack);
        }
    }
}
