package snownee.cuisine.blocks.heat;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.tiles.heat.TileFirePit;
import snownee.kiwi.block.BlockModHorizontal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockFirePit extends BlockModHorizontal implements ITileEntityProvider
{
    public static final AxisAlignedBB AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.25D, 1D);

    public BlockFirePit(String name)
    {
        super(name, Material.ROCK);
        setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH));
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setLightLevel(0.9375F);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState blockState)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = playerIn.getHeldItem(hand);
        TileEntity tile = worldIn.getTileEntity(pos);
        if (stack.getItem() == CuisineRegistry.FAN)
        {
            return false;
        }
        if (hand == EnumHand.MAIN_HAND)
        {
            if (tile instanceof TileFirePit)
            {
                TileFirePit tileFirePit = (TileFirePit) tile;
                if (tileFirePit.stacks.isItemValid(0, stack))
                {
                    ItemStack remain = tileFirePit.stacks.insertItem(0, stack, false);
                    if (!playerIn.isCreative())
                    {
                        playerIn.setHeldItem(hand, remain);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.getBlockState(pos.up()).getBlock().onNeighborChange(worldIn, pos.up(), pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        int heatLevel = 1;
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileFirePit)
        {
            heatLevel = ((TileFirePit) tileEntity).heatHandler.getLevel();
        }

        if (heatLevel > 0 && rand.nextInt(15 - heatLevel * 3) == 0)
        {
            worldIn.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.7F + 0.15F * heatLevel + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }

        for (int i = 0; i < heatLevel; i++)
        {
            float f = (float) (rand.nextFloat() * Math.PI * 2);
            double x = MathHelper.sin(f) * 0.1D;
            double y = pos.getY() + 0.12D + rand.nextDouble() * 0.05D;
            double z = MathHelper.cos(f) * 0.1D;
            if (heatLevel > 1)
            {
                worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.FLAME.getParticleID(), pos.getX() + 0.5D + x, y, pos.getZ() + 0.5D + z, x * 0.2, 0.01 * heatLevel, z * 0.2);
            }
            else
            {
                worldIn.spawnAlwaysVisibleParticle(EnumParticleTypes.FLAME.getParticleID(), pos.getX() + 0.5D + x, y, pos.getZ() + 0.5D + z, 0D, 0D, 0D);
            }
        }
    }

    @Nonnull
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.BOWL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileFirePit();
    }
}
