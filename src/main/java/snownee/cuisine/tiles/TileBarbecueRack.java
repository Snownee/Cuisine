package snownee.cuisine.tiles;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockFirePit;

public class TileBarbecueRack extends TileInventoryBase implements ITickable
{
    private int[] burnTime = new int[3];

    public TileBarbecueRack()
    {
        super(3, 1);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return FurnaceRecipes.instance().getSmeltingResult(stack).getItem() instanceof ItemFood;
    }

    @Override
    public void update()
    {
        if (world.isRemote)
        {
            return;
        }
        for (int i = 0; i < 3; ++i)
        {
            ItemStack stack = stacks.getStackInSlot(i);
            if (!stack.isEmpty() && ++burnTime[i] == 400)
            {
                burnTime[i] = 0;
                ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
                if (!result.isEmpty())
                {
                    stacks.setStackInSlot(i, result.copy());
                }
            }
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        if (oldState.getBlock() != CuisineRegistry.FIRE_PIT || newSate.getBlock() != CuisineRegistry.FIRE_PIT)
        {
            return true;
        }
        else
        {
            return oldState.getValue(BlockFirePit.COMPONENT) != newSate.getValue(BlockFirePit.COMPONENT);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
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
        return tag;
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setIntArray("burnTime", burnTime);
        return super.writePacketData(data);
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        super.readPacketData(data);
        int[] burnTime = data.getIntArray("burnTime");
        if (burnTime.length == 3)
        {
            this.burnTime = burnTime;
        }
    }

    @Override
    public void onContentsChanged(int slot)
    {
        for (int i = 0; i < 3; ++i)
        {
            ItemStack stack = stacks.getStackInSlot(i);
            if (stack.isEmpty())
            {
                burnTime[i] = 0;
            }
        }
        refresh();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return Block.FULL_BLOCK_AABB.offset(pos);
    }
}
