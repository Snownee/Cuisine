package snownee.cuisine.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.cuisine.api.process.BasinInteracting.Output;
import snownee.cuisine.api.process.CuisineProcessingRecipeManager;
import snownee.cuisine.api.process.Processing;

public class TileBasin extends TileInventoryBase
{
    public FluidTank tank = new FluidTank(8000)
    {
        @Override
        protected void onContentsChanged()
        {
            TileBasin.this.onContentsChanged(0);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid)
        {
            if (fluid == null || !canFill() || fluid.getFluid().isGaseous(fluid) || fluid.getFluid().isLighterThanAir())
            {
                return false;
            }
            return TileBasin.this.getClass() != TileBasin.class || fluid.getFluid().getTemperature(fluid) < 500;
        }
    };
    public int tickCheckThrowing = 0;
    private FluidStack liquidForRendering = null;
    boolean squeezingFailed = false;

    public TileBasin()
    {
        super(1);
        tank.setTileEntity(this);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
        }
        return super.getCapability(capability, facing);
    }

    public void spillFluids()
    {
        FluidEvent.fireEvent(new FluidEvent.FluidSpilledEvent(tank.getFluid(), world, pos));
    }

    // @SideOnly(Side.CLIENT) // Left commented so we know that this is for client only
    public FluidStack getFluidForRendering(float partialTicks)
    {
        final FluidStack actual = tank.getFluid();
        int actualAmount;
        if (actual != null && !actual.equals(liquidForRendering))
        {
            liquidForRendering = new FluidStack(actual, 0);
        }
        if (liquidForRendering == null)
        {
            return null;
        }
        actualAmount = actual == null ? 0 : actual.amount;
        int delta = actualAmount - liquidForRendering.amount;
        if (Math.abs(delta) <= 40)
        {
            liquidForRendering.amount = actualAmount;
        }
        else
        {
            int i = (int) (delta * partialTicks * 0.1);
            if (i == 0) // Wow your PC is so powerful!
            {
                i = delta > 0 ? 1 : -1;
            }
            liquidForRendering.amount += i;
        }
        if (liquidForRendering.amount == 0)
        {
            liquidForRendering = null;
        }
        return liquidForRendering;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        tank.readFromNBT(compound.getCompoundTag("tank"));
        if (tank.getFluid() != null)
        {
            liquidForRendering = tank.getFluid().copy();
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        compound.setBoolean("squeezingFailed", squeezingFailed);
        return compound;
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        super.readPacketData(data);
        tank.readFromNBT(data.getCompoundTag("tank"));
        if (data.hasKey("squeezingFailed"))
        {
            squeezingFailed = data.getBoolean("squeezingFailed");
        }
    }

    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        super.writePacketData(data);
        data.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        return writeToNBT(data);
    }

    public void process(CuisineProcessingRecipeManager<BasinInteracting> recipeManager, ItemStack input, boolean simulated)
    {
        if (squeezingFailed && recipeManager == Processing.SQUEEZING)
        {
            return;
        }
        if (input.isEmpty())
        {
            squeezingFailed = true;
            return;
        }
        FluidStack fluid = tank.getFluid();
        BasinInteracting recipe = recipeManager.findRecipe(input, fluid);
        if (recipe != null)
        {
            Output output = recipe.getOutput(input, fluid, world.rand);
            if (output.fluid != null)
            {
                if (output.fluid.amount > tank.getCapacity())
                {
                    squeezingFailed = true;
                    return;
                }
                if (output.fluid.amount <= 0)
                {
                    output.fluid = null;
                }
                if (!simulated)
                {
                    tank.setFluid(output.fluid);
                }
            }
            if (!simulated)
            {
                if (!output.item.isEmpty())
                {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), output.item);
                }
                recipe.consumeInput(input, fluid, world.rand);
            }
            refresh();
        }
        else
        {
            squeezingFailed = true;
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return BasinInteracting.isKnownInput(Processing.SQUEEZING, stack);
    }

    @Override
    public void onContentsChanged(int slot)
    {
        squeezingFailed = false;
        refresh();
    }

    public FluidStack getCurrentFluidContent()
    {
        FluidStack content = this.tank.getFluid();
        if (content == null)
        {
            return null;
        }
        else
        {
            // Never assume people won't do bad things such as manipulating the return value
            return content.copy();
        }
    }

}
