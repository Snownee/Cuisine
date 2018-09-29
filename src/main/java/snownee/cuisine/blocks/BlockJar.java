package snownee.cuisine.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.tiles.TileJar;
import snownee.cuisine.util.StacksUtil;
import snownee.kiwi.block.BlockMod;
import snownee.kiwi.util.InventoryUtil;

@SuppressWarnings("deprecation")
public class BlockJar extends BlockMod
{
    public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 3);

    private static final AxisAlignedBB AABB_TOP = new AxisAlignedBB(0.1875D, 0.6875D, 0.1875D, 0.8125D, 0.9375D, 0.8125D);
    private static final AxisAlignedBB AABB_BOTTOM = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.6875D, 0.9375D);
    private static final AxisAlignedBB AABB_FULL = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.9375D, 0.9375D);

    public BlockJar(String name)
    {
        super(name, Material.CLAY);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(VARIANT, 0));
        setHardness(1.25F);
        setResistance(5.0F);
        setSoundType(SoundType.STONE);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileJar))
        {
            return false;
        }
        ItemStack held = playerIn.getHeldItem(hand);
        if (held.isEmpty())
        {
            if (!worldIn.isRemote && hand == EnumHand.MAIN_HAND)
            {
                StacksUtil.dropInventoryItems(worldIn, pos, ((TileJar) te).stacks, true);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
            else
            {
                return false;
            }
        }
        else
        {
            ItemStack heldCopy = ItemHandlerHelper.copyStackWithSize(held, 1); // do not modify the input
            if (FluidUtil.getFluidHandler(heldCopy) != null)
            {
                return FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing);
            }
            else
            {
                playerIn.setHeldItem(hand, ((TileJar) te).stacks.insertItem(held, false));
                worldIn.updateComparatorOutputLevel(pos, this);
                return true;
            }
        }
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return this.getDefaultState().withProperty(VARIANT, world.rand.nextInt(4));
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(VARIANT);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANT);
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
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_TOP);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BOTTOM);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB_FULL;
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
        return new TileJar();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileJar)
        {
            StacksUtil.dropInventoryItems(worldIn, pos, ((TileJar) te).stacks, true);
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
        if (te instanceof TileJar)
        {
            IItemHandler inv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (inv == null)
            {
                return 0;
            }
            else
            {
                return InventoryUtil.calcRedstoneFromInventory(inv);
            }
        }
        return 0;
    }
}
