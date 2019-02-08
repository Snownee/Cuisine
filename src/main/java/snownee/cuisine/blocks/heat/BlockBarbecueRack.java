package snownee.cuisine.blocks.heat;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.tiles.utensils.TileBarbecueRack;
import snownee.cuisine.util.StacksUtil;
import snownee.kiwi.block.BlockModHorizontal;
import snownee.kiwi.util.AABBUtil;

import javax.annotation.Nullable;
import java.util.List;

public class BlockBarbecueRack extends BlockModHorizontal implements ITileEntityProvider
{
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.4D, 0D, 0D, 0.6D, 1D, 1D);

    public BlockBarbecueRack(String name)
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
        return AABBUtil.rotate(AABB, state.getValue(BlockHorizontal.FACING));
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABBUtil.rotate(BlockFirePit.AABB, state.getValue(BlockHorizontal.FACING)));
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
        RayTraceResult result = rayTrace(pos, start, end, AABBUtil.rotate(AABB, state.getValue(BlockHorizontal.FACING)));
        if (result != null && result.typeOfHit != RayTraceResult.Type.MISS)
        {
            return result;
        }
        return super.collisionRayTrace(state, worldIn, pos, start, end);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = playerIn.getHeldItem(hand);
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileBarbecueRack)
        {
            TileBarbecueRack teBR = (TileBarbecueRack) tile;
            if (teBR.stacks.isItemValid(3, stack))
            {
                ItemStack remain = teBR.stacks.insertItem(3, stack, false);
                if (!playerIn.isCreative())
                {
                    playerIn.setHeldItem(hand, remain);
                }
            }
            else if (!worldIn.isRemote)
            {
                List<AxisAlignedBB> aabbs = Lists.newArrayList();
                EnumFacing facing2 = state.getValue(BlockHorizontal.FACING);
                AxisAlignedBB aabbItem = AABBUtil.rotate(new AxisAlignedBB(0.3D, 0.5D, 0.2D, 0.7D, 0.9D, 0.4D), facing2);
                AxisAlignedBB aabbEmpty = AABBUtil.rotate(new AxisAlignedBB(0.45D, 0.65D, 0.2D, 0.55D, 0.75D, 0.4D), facing2);
                for (int i = 0; i < 3; i++)
                {
                    aabbs.add((teBR.stacks.getStackInSlot(2 - i).isEmpty() ? aabbEmpty : aabbItem).offset(facing2.getDirectionVec().getX() * 0.2 * i, 0, facing2.getOpposite().getDirectionVec().getZ() * 0.2 * i).offset(pos));
                }
                int result = AABBUtil.rayTraceByDistance(playerIn, aabbs);
                if (result != -1)
                {
                    result = 2 - result;
                    ItemStack stackSlot = teBR.stacks.extractItem(result, Integer.MAX_VALUE, false);
                    if (stackSlot.isEmpty())
                    {
                        if (!stack.isEmpty())
                        {
                            ItemStack remain = teBR.stacks.insertItem(result, playerIn.isCreative() ? stack.copy() : stack, false);
                            if (!playerIn.isCreative())
                            {
                                playerIn.setHeldItem(hand, remain);
                            }
                        }
                    }
                    else
                    {
                        EntityItem entityitem = new EntityItem(worldIn, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stackSlot);
                        entityitem.motionX = 0;
                        entityitem.motionY = 0;
                        entityitem.motionZ = 0;
                        worldIn.spawnEntity(entityitem);
                        if (!(playerIn instanceof FakePlayer))
                        {
                            entityitem.onCollideWithPlayer(playerIn);
                        }
                    }
                }
            }

            return true;
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileBarbecueRack)
        {
            StacksUtil.dropInventoryItems(worldIn, pos, ((TileBarbecueRack) tileentity).stacks, true);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileBarbecueRack();
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileBarbecueRack)
        {
            TileBarbecueRack teBR = (TileBarbecueRack) te;
            int output = 0;
            for (int i = 0; i < 3; ++i)
            {
                ItemStack stack = teBR.stacks.getStackInSlot(i);
                if (!stack.isEmpty())
                {
                    output += teBR.stacks.isItemValid(0, stack) ? 1 : 5;
                }
            }
            return output;
        }
        return 0;
    }
}
