package snownee.cuisine.blocks.utensils;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.IHeatable;
import snownee.cuisine.tiles.utensils.TileHeatingUtensil;
import snownee.cuisine.tiles.utensils.TileUtensil;
import snownee.kiwi.block.BlockModHorizontal;
import snownee.kiwi.util.AABBUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Represents a utensil block
 */
public class BlockUtensil extends BlockModHorizontal implements ITileEntityProvider
{
    protected final AxisAlignedBB boundingBox;
    protected final Supplier<TileEntity> provider;

    public BlockUtensil(String name, Supplier<TileEntity> provider, AxisAlignedBB boundingBox)
    {
        super(name, Material.IRON);
        this.provider = provider;
        this.boundingBox = boundingBox;
        setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH));
        setCreativeTab(Cuisine.CREATIVE_TAB);
        // Utensils shall not have light levels
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockHorizontal.FACING);
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
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABBUtil.rotate(boundingBox, state.getValue(BlockHorizontal.FACING));
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
        RayTraceResult result = rayTrace(pos, start, end, getBoundingBox(state, worldIn, pos));
        if (result != null && result.typeOfHit != RayTraceResult.Type.MISS)
        {
            return result;
        }
        return super.collisionRayTrace(state, worldIn, pos, start, end);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(BlockHorizontal.FACING).getHorizontalIndex();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        if (neighbor.equals(pos.down()))
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            TileEntity heaterEntity = world.getTileEntity(neighbor);
            if (tileEntity instanceof TileHeatingUtensil)
            {
                if (heaterEntity instanceof IHeatable)
                    ((TileHeatingUtensil) tileEntity).updateNearby((IHeatable) heaterEntity);
                else
                    ((TileHeatingUtensil) tileEntity).updateNearby(null);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileUtensil && !worldIn.isRemote && playerIn instanceof EntityPlayerMP && hand == EnumHand.MAIN_HAND) {
            ((TileUtensil) tileEntity).onActivated(playerIn, hand, facing);
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        TileEntity heaterEntity = world.getTileEntity(pos.down());
        if (tileEntity instanceof TileHeatingUtensil)
        {
            if (heaterEntity instanceof IHeatable)
                ((TileHeatingUtensil) tileEntity).updateNearby((IHeatable) heaterEntity);
            else
                ((TileHeatingUtensil) tileEntity).updateNearby(null);
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta)
    {
        return provider.get();
    }
}
