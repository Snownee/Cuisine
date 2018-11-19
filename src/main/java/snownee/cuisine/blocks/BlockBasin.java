package snownee.cuisine.blocks;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.tiles.TileBasin;
import snownee.cuisine.tiles.TileBasinHeatable;
import snownee.cuisine.util.StacksUtil;
import snownee.kiwi.block.BlockMod;

public class BlockBasin extends BlockMod
{
    public static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);

    public BlockBasin(String name, Material materialIn)
    {
        super(name, materialIn);
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        entityIn.fall(fallDistance, 0.5F);

        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileBasin)
        {
            TileBasin tileBasin = ((TileBasin) tile);
            if (fallDistance >= 1 && entityIn instanceof EntityLivingBase)
            {
                ItemStack input = tileBasin.stacks.getStackInSlot(0);
                if (!input.isEmpty() || tileBasin.tank.getFluidAmount() > 0)
                {
                    worldIn.playSound(null, pos, SoundEvents.BLOCK_SLIME_STEP, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() / 4 + .6F);
                }
                if (input.isEmpty())
                {
                    return;
                }
                if (input.getItem() == Item.getItemFromBlock(Blocks.CACTUS))
                {
                    entityIn.attackEntityFrom(DamageSource.CACTUS, 1);
                }
                else if (input.getItem() == CuisineRegistry.BASIC_FOOD && input.getMetadata() == ItemBasicFood.Variants.EMPOWERED_CITRON.getMeta() && entityIn instanceof EntityPlayer && tileBasin.tank.getFluidAmount() == 0)
                {
                    ItemBasicFood.citronSays((EntityLivingBase) entityIn, "squeeze");
                }
                tileBasin.process(Processing.SQUEEZING, input, false);
                if (entityIn instanceof EntityIronGolem)
                {
                    tileBasin.process(Processing.SQUEEZING, input, false);
                }
            }
        }
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (worldIn.isRemote)
        {
            return;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileBasin)
        {
            TileBasin tileBasin = ((TileBasin) tile);
            if (entityIn.getClass() == EntityItem.class)
            {
                if (tileBasin.tickCheckThrowing > 0)
                {
                    tileBasin.tickCheckThrowing--;
                    return;
                }
                List<ItemStack> items = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos)).stream().filter(e -> !e.isDead && e.onGround).map(EntityItem::getItem).collect(Collectors.toList());
                for (ItemStack stack : items)
                {
                    tileBasin.process(Processing.BASIN_THROWING, stack, false);
                }
                tileBasin.tickCheckThrowing = 25;
            }
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return state.getMaterial() == Material.WOOD ? new TileBasin() : new TileBasinHeatable();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileBasin)
        {
            StacksUtil.dropInventoryItems(worldIn, pos, ((TileBasin) te).stacks, true);
            ((TileBasin) te).spillFluids();
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileBasin)
        {
            ItemStack held = playerIn.getHeldItem(hand);
            if (facing == EnumFacing.UP && held.getItem() == Item.getItemFromBlock(Blocks.PISTON))
            {
                return false;
            }
            TileBasin tileBasin = (TileBasin) tile;
            ItemStack inv = tileBasin.stacks.getStackInSlot(0);
            if (held.isEmpty())
            {
                if (inv.isEmpty())
                {
                    return false;
                }
                else
                {
                    StacksUtil.dropInventoryItems(worldIn, pos, tileBasin.stacks, false);
                    return true;
                }
            }
            else
            {
                ItemStack heldCopy = ItemHandlerHelper.copyStackWithSize(held, 1); // do not modify the input
                if (FluidUtil.getFluidHandler(heldCopy) != null)
                {
                    FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing);
                }
                else if (inv.isEmpty())
                {
                    playerIn.setHeldItem(hand, tileBasin.stacks.insertItem(0, held, false));
                }
                else
                {
                    StacksUtil.dropInventoryItems(worldIn, pos, tileBasin.stacks, false);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        IFluidHandler handler = FluidUtil.getFluidHandler(worldIn, pos, null);
        if (handler != null && handler.getTankProperties().length > 0)
        {
            IFluidTankProperties tank = handler.getTankProperties()[0];
            if (tank.getContents() != null)
            {
                return 1 + tank.getContents().amount * 14 / tank.getCapacity();
            }
        }
        return 0;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        neighborChanged(state, worldIn, pos, this, pos.up());
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (pos.up().equals(fromPos))
        {
            IBlockState fromState = worldIn.getBlockState(fromPos);
            if (fromState.getBlock() == Blocks.PISTON)
            {
                worldIn.setBlockState(fromPos, CuisineRegistry.SQUEEZER.getDefaultState());
            }
        }
        else if (pos.down().equals(fromPos))
        {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileBasinHeatable)
            {
                ((TileBasinHeatable) tile).onContentsChanged(0);
            }
        }
    }

    @Override
    public void fillWithRain(World worldIn, BlockPos pos)
    {
        IFluidHandler handler = FluidUtil.getFluidHandler(worldIn, pos, EnumFacing.UP);
        if (handler != null)
        {
            handler.fill(new FluidStack(FluidRegistry.WATER, 100), true);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
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
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.DOWN;
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
}
