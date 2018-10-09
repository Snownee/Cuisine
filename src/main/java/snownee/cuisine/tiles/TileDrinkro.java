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
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.blocks.BlockDrinkro;
import snownee.cuisine.fluids.CuisineFluids;
import snownee.cuisine.internal.food.Drink;

public class TileDrinkro extends TileBase implements CookingVessel
{
    protected static class DrinkroFluidWrapper implements IFluidHandler
    {
        private final TileDrinkro tile;

        public DrinkroFluidWrapper(TileDrinkro tile)
        {
            this.tile = tile;
        }

        @Override
        public IFluidTankProperties[] getTankProperties()
        {
            return new IFluidTankProperties[0];
        }

        @Override
        public int fill(FluidStack resource, boolean doFill)
        {
            if (resource == null)
            {
                return 0;
            }
            // 1 size = 500mB, fine-tuning needed
            if (resource.getFluid() == CuisineFluids.DRINK)
            {
                // TODO
                return 0;
            }
            Material material = CulinaryHub.API_INSTANCE.findMaterial(resource);
            if (material != null)
            {
                float quantity = resource.amount / 500F;
                Ingredient ingredient = new Ingredient(material, Form.JUICE, quantity);
                if (doFill)
                {
                    if (tile.builder.addIngredient(null, ingredient, tile)) // FIXME: Nullable?!
                    {
                        return resource.amount;
                    }
                }
                else
                {
                    if (tile.builder.canAddIntoThis(null, ingredient, tile)) // FIXME: Nullable?!
                    {
                        return resource.amount;
                    }
                }
            }
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
    protected Drink.Builder builder;
    private ItemStackHandler input = new ItemStackHandler(4)
    {
        public int getSlotLimit(int slot)
        {
            return 1;
        };

        public boolean isItemValid(int slot, ItemStack stack)
        {
            if (builder.isFeatureItem(stack))
            {
                // One drink can't have two or more feature items
                for (ItemStack stack2 : stacks)
                {
                    if (builder.isFeatureItem(stack2))
                    {
                        return false;
                    }
                }
                return true;
            }
            return CulinaryHub.API_INSTANCE.isKnownSpice(stack);
        };
    };
    private ItemStackHandler output = new ItemStackHandler()
    {
        public int getSlotLimit(int slot)
        {
            return 1;
        };
    };

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
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new DrinkroFluidWrapper(this));
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
