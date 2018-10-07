package snownee.cuisine.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.blocks.BlockDrinkro;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.tiles.TileInventoryBase.StackHandler;

public class TileDrinkro extends TileBase implements CookingVessel
{
    protected static class DrinkroFluidWrapper implements IFluidHandler
    {

        @Override
        public IFluidTankProperties[] getTankProperties()
        {
            return new IFluidTankProperties[0];
        }

        @Override
        public int fill(FluidStack resource, boolean doFill)
        {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            return null;
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            return null;
        }

    }

    protected static class DrinkroItemWrapper implements IItemHandler
    {
        private final IItemHandler input, output;

        public DrinkroItemWrapper(IItemHandler input, IItemHandler output)
        {
            this.input = input;
            this.output = output;
        }

        @Override
        public int getSlots()
        {
            return input.getSlots() + output.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot)
        {
            if (slot < input.getSlots())
            {
                return input.getStackInSlot(slot);
            }
            else
            {
                return output.getStackInSlot(slot - input.getSlots());
            }
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            if (slot < input.getSlots())
            {
                return input.insertItem(slot, stack, simulate);
            }
            else
            {
                return output.insertItem(slot - input.getSlots(), stack, simulate);
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (slot < input.getSlots())
            {
                return ItemStack.EMPTY;
            }
            else
            {
                return input.extractItem(slot, amount, simulate);
            }
        }

        @Override
        public int getSlotLimit(int slot)
        {
            if (slot < input.getSlots())
            {
                return input.getSlotLimit(slot);
            }
            else
            {
                return output.getSlotLimit(slot - input.getSlots());
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack)
        {
            if (slot < input.getSlots())
            {
                return input.isItemValid(slot, stack);
            }
            else
            {
                return output.isItemValid(slot - input.getSlots(), stack);
            }
        }
    }

    private boolean powered = false;
    private Drink.Builder builder;
    private ItemStackHandler input = new ItemStackHandler();
    private ItemStackHandler output = new ItemStackHandler();

    public void neighborChanged(IBlockState state)
    {
        boolean poweredIn = world.isBlockPowered(pos);
        if (poweredIn && !this.powered)
        {
            world.setBlockState(pos, state.withProperty(BlockDrinkro.WORKING, true));
            world.scheduleUpdate(pos, state.getBlock(), 100);
        }
        this.powered = poweredIn;
    }

    public void stopProcess()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new DrinkroFluidWrapper());
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new DrinkroItemWrapper(input, output));
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        return data;
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        // TODO Auto-generated method stub
        return data;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public int getTemperature()
    {
        return 0;
    }

    @Override
    public int getWaterAmount()
    {
        return 0;
    }

    @Override
    public int getOilAmount()
    {
        return 0;
    }

}
