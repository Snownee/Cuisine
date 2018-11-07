package snownee.cuisine.blocks;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.tiles.TileMill;
import snownee.cuisine.util.StacksUtil;
import snownee.kiwi.block.BlockModHorizontal;

@SuppressWarnings("deprecation")
public class BlockMill extends BlockModHorizontal
{
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.525, 0.875);

    public BlockMill(String name)
    {
        super(name, Material.ROCK);
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        this.setDefaultDirection(worldIn, pos, state);
    }

    private void setDefaultDirection(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            EnumFacing enumfacing = state.getValue(BlockHorizontal.FACING);
            boolean flag = worldIn.getBlockState(pos.north()).isFullBlock();
            boolean flag1 = worldIn.getBlockState(pos.south()).isFullBlock();

            if (enumfacing == EnumFacing.NORTH && flag && !flag1)
            {
                enumfacing = EnumFacing.SOUTH;
            }
            else if (enumfacing == EnumFacing.SOUTH && flag1 && !flag)
            {
                enumfacing = EnumFacing.NORTH;
            }
            else
            {
                boolean flag2 = worldIn.getBlockState(pos.west()).isFullBlock();
                boolean flag3 = worldIn.getBlockState(pos.east()).isFullBlock();

                if (enumfacing == EnumFacing.WEST && flag2 && !flag3)
                {
                    enumfacing = EnumFacing.EAST;
                }
                else if (enumfacing == EnumFacing.EAST && flag3 && !flag2)
                {
                    enumfacing = EnumFacing.WEST;
                }
            }

            worldIn.setBlockState(pos, state.withProperty(BlockHorizontal.FACING, enumfacing), 2);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileMill))
        {
            return false;
        }

        ItemStack held = playerIn.getHeldItem(hand);
        if (held.isEmpty())
        {
            if (hand == EnumHand.MAIN_HAND)
            {
                if (playerIn.isSneaking())
                {
                    if (!worldIn.isRemote)
                    {
                        StacksUtil.spawnItemStack(worldIn, pos, ((TileMill) te).getItemContent(), true);
                        worldIn.updateComparatorOutputLevel(pos, this);
                    }
                }
                else
                {
                    ((TileMill) te).onRightClicked(playerIn);
                }
            }
            else
            {
                return false;
            }
        }
        else if (!worldIn.isRemote)
        {
            if (FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing))
            {
                return true;
            }
            else
            {
                playerIn.setHeldItem(hand, ((TileMill) te).tryInsertItem(held));
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }

        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[] { BlockHorizontal.FACING, Properties.StaticProperty }, new IUnlistedProperty[] { Properties.AnimationProperty });
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World worldIn, IBlockState state)
    {
        return new TileMill();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileMill)
        {
            StacksUtil.spawnItemStack(worldIn, pos, ((TileMill) tileentity).getItemContent(), true);
            ((TileMill) tileentity).spillFluids();
        }

        super.breakBlock(worldIn, pos, state);
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
        if (te instanceof TileMill && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
        {
            return ItemHandlerHelper.calcRedstoneFromInventory(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
        }
        return 0;
    }
}
