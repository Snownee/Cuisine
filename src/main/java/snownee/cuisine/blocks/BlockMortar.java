package snownee.cuisine.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.items.ItemMortar;
import snownee.cuisine.tiles.TileMortar;
import snownee.cuisine.util.StacksUtil;
import snownee.kiwi.block.BlockModHorizontal;

@SuppressWarnings("deprecation")
public class BlockMortar extends BlockModHorizontal
{

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.1875D, 0.0625D, 0.1875D, 0.8125D, 0.3125D, 0.8125D);

    private static final PropertyBool PESTLE_DOWN = PropertyBool.create("pestle");

    public BlockMortar(String name)
    {
        super(name, Material.ROCK);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setHardness(1.0F);
        setDefaultState(super.getDefaultState().withProperty(PESTLE_DOWN, Boolean.FALSE));
    }

    @Override
    public boolean hasItem()
    {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockHorizontal.FACING, PESTLE_DOWN);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        // We do not serialize this field for backward compatibility reason
        // As result, we query it via TileEntity.
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileMortar)
        {
            return state.withProperty(PESTLE_DOWN, ((TileMortar) tile).pestle ? Boolean.TRUE : Boolean.FALSE);
        }
        return state.withProperty(PESTLE_DOWN, Boolean.FALSE);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileMortar)
        {
            TileMortar mortar = (TileMortar) te;
            ItemStack heldItem = playerIn.getHeldItem(hand);
            if (heldItem.isEmpty())
            {
                if (playerIn.isSneaking())
                {
                    StacksUtil.dropInventoryItems(worldIn, pos, mortar.stacks, false);
                }
                else
                {
                    playerIn.addExhaustion(0.1F);
                    mortar.process(playerIn);
                    // Call this to sync data to client
                    worldIn.notifyBlockUpdate(pos, state, state, 3);
                }
            }
            else
            {
                if (heldItem.getItem() != CuisineRegistry.ITEM_MORTAR)
                {
                    heldItem = mortar.insertItem(heldItem);
                    playerIn.setHeldItem(hand, heldItem);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileMortar)
        {
            StacksUtil.dropInventoryItems(worldIn, pos, ((TileMortar) te).stacks, true);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileMortar();
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
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
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return CuisineRegistry.ITEM_MORTAR.getItemStack(ItemMortar.Variants.EMPTY);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return CuisineRegistry.ITEM_MORTAR;
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
        if (te instanceof TileMortar && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
        {
            return ItemHandlerHelper.calcRedstoneFromInventory(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
        }
        return 0;
    }
}
