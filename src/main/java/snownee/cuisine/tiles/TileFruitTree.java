package snownee.cuisine.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import snownee.cuisine.blocks.BlockModLeaves;
import snownee.cuisine.blocks.BlockModSapling;

public class TileFruitTree extends TileBase
{
    public BlockModSapling.Type type = BlockModSapling.Type.CITRON;

    public TileFruitTree()
    {
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        if (oldState.getBlock() instanceof BlockModLeaves && newState.getBlock() instanceof BlockModLeaves)
        {
            return oldState.getValue(BlockModLeaves.CORE) != newState.getValue(BlockModLeaves.CORE);
        }
        else
        {
            return true;
        }
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
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("type", Constants.NBT.TAG_INT))
        {
            BlockModSapling.Type[] types = BlockModSapling.Type.values();
            type = types[MathHelper.clamp(compound.getInteger("type"), 0, types.length)];
        }
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("type", type.ordinal());
        return compound;
    }

}
