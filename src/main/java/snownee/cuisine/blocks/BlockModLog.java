package snownee.cuisine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import snownee.cuisine.Cuisine;
import snownee.kiwi.block.IModBlock;

public class BlockModLog extends BlockLog implements IModBlock
{
    private final String name;

    public BlockModLog(String name)
    {
        this.name = name;
        this.setCreativeTab(Cuisine.CREATIVE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
    }

    @Override
    public Block cast()
    {
        return this;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void register(String modid)
    {
        setRegistryName(modid, getName());
        setTranslationKey(modid + "." + getName());
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, LOG_AXIS);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(LOG_AXIS, EnumAxis.values()[meta % 4]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(LOG_AXIS).ordinal();
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return 5;
    }

}
