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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.NBTHelper.Tag;

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
                    return FuelHeatHandler.isFuel(stack, true);
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
                        NBTHelper helper = NBTHelper.of(stack);
                        int doneness = helper.getInt(CuisineSharedSecrets.KEY_DONENESS);
                        helper.setInt(CuisineSharedSecrets.KEY_DONENESS, ++doneness);
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
                            if (!stacks.isItemValid(0, result))
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
        NBTHelper helper = NBTHelper.of(compound);
        stacks.deserializeNBT(helper.getTag("Items", true));
        refreshEmpty();
        if (helper.hasTag("burnTime", Tag.INT_ARRAY))
        {
            int[] burnTime = helper.getIntArray("burnTime");
            if (burnTime.length == 3)
            {
                this.burnTime = burnTime;
            }
        }
        if (helper.hasTag("completed", Tag.INT_ARRAY))
        {
            int[] arr = helper.getIntArray("completed");
            if (arr.length == 3)
            {
                for (int i = 0; i < arr.length; i++)
                {
                    completed[i] = arr[i] > 0;
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        NBTTagCompound tag = super.writeToNBT(compound);
        tag.setIntArray("burnTime", burnTime);
        int[] arr = new int[3];
        for (int i = 0; i < completed.length; i++)
        {
            arr[i] = completed[i] ? 1 : 0;
        }
        tag.setIntArray("completed", arr);
        tag.setTag("Items", this.stacks.serializeNBT());
        return tag;
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        return writeToNBT(data);
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        readFromNBT(data);
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
