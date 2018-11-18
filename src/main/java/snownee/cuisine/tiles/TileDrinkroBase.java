package snownee.cuisine.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockDrinkro;
import snownee.cuisine.internal.food.Drink;

public class TileDrinkroBase extends TileBase
{
    private boolean powered = false;
    public ItemStackHandler inventory;

    public TileDrinkroBase()
    {
        super();
        inventory = new ItemStackHandler()
        {
            @Override
            public int getSlotLimit(int slot)
            {
                return 1;
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack)
            {
                return Drink.Builder.isContainerItem(stack);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
            {
                if (!isItemValid(slot, stack))
                {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            protected void onContentsChanged(int slot)
            {
                refresh();
            }
        };
    }

    protected TileDrinkroTank getTank()
    {
        if (hasWorld())
        {
            TileEntity tile = world.getTileEntity(pos.up());
            if (tile instanceof TileDrinkroTank)
            {
                return (TileDrinkroTank) tile;
            }
        }
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY && CuisineConfig.GENERAL.drinkroUsesFE > 0)
        {
            TileDrinkroTank tile = getTank();
            return tile == null ? false : tile.hasCapability(capability, facing);
        }
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && getTank() != null) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            TileDrinkroTank tile = getTank();
            return tile == null ? null : tile.getCapability(capability, facing);
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        }
        if (capability == CapabilityEnergy.ENERGY && CuisineConfig.GENERAL.drinkroUsesFE > 0)
        {
            TileDrinkroTank tile = getTank();
            return tile == null ? null : tile.getCapability(capability, facing);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
        inventory.deserializeNBT(data.getCompoundTag("inventory"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setTag("inventory", inventory.serializeNBT());
        return data;
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        this.inventory.deserializeNBT(data.getCompoundTag("inventory"));
    }

    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setTag("inventory", this.inventory.serializeNBT());
        return data;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        if (oldState.getBlock() != CuisineRegistry.DRINKRO || newState.getBlock() != CuisineRegistry.DRINKRO)
        {
            return true;
        }
        else
        {
            return oldState.getValue(BlockDrinkro.BASE) != newState.getValue(BlockDrinkro.BASE);
        }
    }

    protected void refresh()
    {
        if (hasWorld() && !world.isRemote)
        {
            IBlockState state = world.getBlockState(pos);
            world.markAndNotifyBlock(pos, null, state, state, 11);
        }
    }

}
