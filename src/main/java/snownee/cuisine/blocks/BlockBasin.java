package snownee.cuisine.blocks;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.tiles.TileBasin;
import snownee.cuisine.tiles.TileBasinHeatable;
import snownee.cuisine.util.StacksUtil;
import snownee.kiwi.block.BlockMod;
import snownee.kiwi.util.InventoryUtil;

public class BlockBasin extends BlockMod
{

    public BlockBasin(String name, Material materialIn)
    {
        super(name, materialIn);
        setHardness(2.0F);
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
            if (entityIn.getClass() == EntityItem.class)
            {
                List<ItemStack> items = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.up())).stream().map(EntityItem::getItem).map(ItemStack::copy).collect(Collectors.toList());
                for (ItemStack stack : InventoryUtil.mergeItemStacks(items, false))
                {
                    tileBasin.process(Processing.BASIN_THROWING, stack);
                    // TODO: consume
                }
            }
            else if (fallDistance >= 1)
            {
                ItemStack input = tileBasin.stacks.getStackInSlot(0);
                if (input.getItem() == Item.getItemFromBlock(Blocks.CACTUS))
                {
                    entityIn.attackEntityFrom(DamageSource.CACTUS, 1);
                }
                tileBasin.process(Processing.SQUEEZING, input);
                if (entityIn instanceof EntityIronGolem)
                {
                    tileBasin.process(Processing.SQUEEZING, input);
                }
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
        return state.getMaterial() != Material.WOOD ? new TileBasin() : new TileBasinHeatable();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileBasin)
        {
            StacksUtil.dropInventoryItems(worldIn, pos, ((TileBasin) te).stacks, true);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileBasin)
        {
            TileBasin tileBasin = (TileBasin) tile;
            ItemStack held = playerIn.getHeldItem(hand);
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
}
