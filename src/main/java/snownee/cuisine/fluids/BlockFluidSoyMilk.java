package snownee.cuisine.fluids;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import snownee.cuisine.CuisineRegistry;

public class BlockFluidSoyMilk extends BlockFluid
{
    public BlockFluidSoyMilk(final Fluid fluid)
    {
        super(fluid, "soy_milk", new MaterialLiquid(MapColor.SNOW));
        setTickRandomly(true);
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

    public void checkMixing(@Nonnull IBlockState state, World world, BlockPos pos, @Nonnull BlockPos neighbourPos)
    {
        Block block = world.getBlockState(neighbourPos).getBlock();
        if (isSourceBlock(world, pos) && (block == Blocks.WATER || block == Blocks.FLOWING_WATER))
        {
            world.setBlockState(pos, CuisineRegistry.TOFU_BLOCK.getDefaultState());
        }
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        super.onEntityCollision(worldIn, pos, state, entityIn);
        if (entityIn instanceof EntityLivingBase)
        {
            ((EntityLivingBase) entityIn).curePotionEffects(new ItemStack(Items.MILK_BUCKET));
        }
    }

    @Override
    public void fillWithRain(World worldIn, BlockPos pos)
    {
        if (!worldIn.isRemote && isSourceBlock(worldIn, pos))
        {
            worldIn.setBlockState(pos, CuisineRegistry.TOFU_BLOCK.getDefaultState());
        }
    }
}
