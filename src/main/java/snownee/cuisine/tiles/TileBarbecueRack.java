package snownee.cuisine.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.cuisine.util.ItemNBTUtil;

public class TileBarbecueRack extends TileFirePit
{
    public final ItemStackHandler stacks;
    public int[] burnTime = new int[3];
    public boolean[] completed = new boolean[3];
    private boolean isEmpty;

    public TileBarbecueRack()
    {
        stacks = new ItemStackHandler(4)
        {
            @Override
            public int getSlotLimit(int slot)
            {
                return 1;
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
            {
                if (!isItemValid(slot, stack))
                {
                    return stack;
                }
                if (slot == 3)
                {
                    return heatHandler.addFuel(stack);
                }
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack)
            {
                if (slot < 3)
                {
                    return stack.getItem() == CuisineRegistry.INGREDIENT || FurnaceRecipes.instance().getSmeltingResult(stack).getItem() instanceof ItemFood;
                }
                else
                {
                    return FuelHeatHandler.isFuel(stack);
                }
            }

            @Override
            protected void onContentsChanged(int slot)
            {
                for (int i = 0; i < 3; ++i)
                {
                    ItemStack stack = getStackInSlot(i);
                    if (stack.isEmpty())
                    {
                        burnTime[i] = 0;
                        completed[i] = false;
                    }
                }
                refreshEmpty();
                refresh();
            }
        };
    }

    @Override
    public void update()
    {
        super.update();
        if (world.isRemote)
        {
            return;
        }
        int heatLevel = heatHandler.getLevel();
        if (heatLevel > 0)
        {
            for (int i = 0; i < 3; ++i)
            {
                ItemStack stack = stacks.getStackInSlot(i);
                if (stack.isEmpty())
                {
                    continue;
                }
                burnTime[i] += heatLevel;
                if (stack.getItem() == CuisineRegistry.INGREDIENT)
                {
                    if (burnTime[i] >= 6)
                    {
                        burnTime[i] = 0;
                        int doneness = ItemNBTUtil.getInt(stack, CuisineSharedSecrets.KEY_DONENESS, 0);
                        ItemNBTUtil.setInt(stack, CuisineSharedSecrets.KEY_DONENESS, ++doneness);
                    }
                }
                else
                {
                    if (burnTime[i] >= 800)
                    {
                        burnTime[i] = 0;
                        ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
                        if (!result.isEmpty())
                        {
                            stacks.setStackInSlot(i, result.copy());
                            if (!stacks.isItemValid(0, stack))
                            {
                                completed[i] = true;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        stacks.deserializeNBT(compound.getCompoundTag("Items"));
        refreshEmpty();
        if (compound.hasKey("burnTime", Constants.NBT.TAG_INT_ARRAY))
        {
            int[] burnTime = compound.getIntArray("burnTime");
            if (burnTime.length == 3)
            {
                this.burnTime = burnTime;
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound tag = super.writeToNBT(compound);
        tag.setIntArray("burnTime", burnTime);
        //        int[] arr = new int[3];
        //        for (int i = 0; i < completed.length; i++)
        //        {
        //            arr[i] = completed[i] ? 1 : 0;
        //        }
        //        tag.setIntArray("completed", arr);
        tag.setTag("Items", this.stacks.serializeNBT());
        return tag;
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        super.writePacketData(data);
        data.setTag("Items", this.stacks.serializeNBT());
        return data;
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        super.readPacketData(data);
        stacks.deserializeNBT(data.getCompoundTag("Items"));
        refreshEmpty();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(stacks);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return Block.FULL_BLOCK_AABB.offset(pos);
    }

    public boolean isEmpty()
    {
        return isEmpty;
    }

    private void refreshEmpty()
    {
        isEmpty = true;
        for (int i = 0; i < 3; i++)
        {
            if (!stacks.getStackInSlot(i).isEmpty())
            {
                isEmpty = false;
                break;
            }
        }

    }
}
