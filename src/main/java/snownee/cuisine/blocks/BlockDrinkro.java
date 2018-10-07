package snownee.cuisine.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.tiles.TileDrinkro;
import snownee.kiwi.block.BlockModHorizontal;

public class BlockDrinkro extends BlockModHorizontal
{
    public static final PropertyBool NORMAL = PropertyBool.create("normal");
    public static final PropertyBool WORKING = PropertyBool.create("working");

    public BlockDrinkro(String name)
    {
        super(name, Material.IRON);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(NORMAL, true).withProperty(WORKING, false));
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileDrinkro();
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!state.getValue(WORKING))
        {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileDrinkro)
            {
                ((TileDrinkro) tile).neighborChanged(state);
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (state.getValue(WORKING))
        {
            worldIn.setBlockState(pos, state.withProperty(WORKING, false));
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileDrinkro)
            {
                ((TileDrinkro) tile).stopProcess();
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
//        if (stateIn.getValue(WORKING))
//        {
//            worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.SPELL.getParticleID(), pos.getX() + 0.5, pos.getY() + 1, pos.getZ(), 0xFF, 0, 0, 0xFFFF00FF);
//            worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0xFF, 0, 0, 0xFFFF00FF);
//            worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.SPELL_INSTANT.getParticleID(), pos.getX(), pos.getY() + 1, pos.getZ() + 0.5, 0xFF, 0, 0, 0xFFFF00FF);
//            worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, 0xFF, 0, 0, 0xFFFF00FF);
//        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(NORMAL, meta == 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return super.getStateFromMeta(meta).withProperty(NORMAL, (meta & 7) < 4).withProperty(WORKING, meta < 8);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return super.getMetaFromState(state) + (state.getValue(NORMAL) ? 0 : 4) + (state.getValue(WORKING) ? 0 : 8);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockHorizontal.FACING, NORMAL, WORKING);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return super.shouldSideBeRendered(blockState, worldIn, pos, side);
    }

}
