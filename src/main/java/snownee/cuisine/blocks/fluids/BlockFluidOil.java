package snownee.cuisine.blocks.fluids;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidOil extends BlockFluid
{
    public BlockFluidOil(Fluid fluid, String name)
    {
        super(fluid, name, new MaterialLiquid(MapColor.YELLOW_STAINED_HARDENED_CLAY));
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return 200;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return 2000;
    }

    public void checkMixing(@Nonnull IBlockState state, World world, BlockPos pos, @Nonnull BlockPos neighbourPos)
    {
        IBlockState neighbourState = world.getBlockState(neighbourPos);
        if (neighbourState.getBlock() == Blocks.MAGMA || neighbourState.getMaterial() == Material.LAVA && neighbourState.getValue(LEVEL) == 0)
        {
            world.setBlockToAir(neighbourPos);
            world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3.0F, true, true);
        }
    }

    @Override
    public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock, @Nonnull BlockPos neighbourPos)
    {
        super.neighborChanged(state, world, pos, neighborBlock, neighbourPos);
        checkMixing(state, world, pos, neighbourPos);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(world, pos, state);
        for (EnumFacing side : EnumFacing.VALUES)
        {
            BlockPos neighbourPos = pos.offset(side);
            checkMixing(state, world, pos, neighbourPos);
        }
    }
}
