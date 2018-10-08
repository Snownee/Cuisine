package snownee.cuisine.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.tiles.TileBasin;
import snownee.cuisine.tiles.TileSqueezer;
import snownee.kiwi.block.BlockMod;

import javax.annotation.Nullable;

public class BlockSqueezer extends BlockMod
{

    public static final AxisAlignedBB AABB = new AxisAlignedBB(0.0625, 0.0625, 0.0625, 0.9375, 0.9375, 0.9375);

    public BlockSqueezer(String name)
    {
        super(name, Material.PISTON);
        setHardness(0.5F);
        setCreativeTab(Cuisine.CREATIVE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(BlockDispenser.TRIGGERED, false));
    }

    @Override
    public boolean hasItem()
    {
        return false;
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        neighborChanged(state, worldIn, pos, Blocks.AIR, pos.down());
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (pos.down().equals(fromPos))
        {
            IBlockState fromState = worldIn.getBlockState(fromPos);
            if (!(fromState.getBlock() instanceof BlockBasin))
            {
                worldIn.setBlockState(pos, Blocks.PISTON.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.DOWN));
                return;
            }
        }

        boolean pre = state.getValue(BlockDispenser.TRIGGERED);
        boolean post = worldIn.isBlockPowered(pos);
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileSqueezer))
        {
            return;
        }
        TileSqueezer squeezer = (TileSqueezer) tile;
        if (pre && !post)
        {
            worldIn.setBlockState(pos, state.withProperty(BlockDispenser.TRIGGERED, Boolean.FALSE));
            squeezer.onTriggered(false);
        }
        else if (!pre && post)
        {
            worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
            worldIn.setBlockState(pos, state.withProperty(BlockDispenser.TRIGGERED, Boolean.TRUE));
            squeezer.onTriggered(true);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        TileEntity tile = worldIn.getTileEntity(pos.down());
        if (tile instanceof TileBasin)
        {
            TileBasin tileBasin = (TileBasin) tile;
            tileBasin.process(Processing.SQUEEZING, tileBasin.stacks.getStackInSlot(0));
        }
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
        return new TileSqueezer();
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
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(Blocks.PISTON);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(Blocks.PISTON);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(BlockDispenser.TRIGGERED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(BlockDispenser.TRIGGERED) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this,
                new IProperty<?>[] { BlockDispenser.TRIGGERED, Properties.StaticProperty },
                new IUnlistedProperty<?>[] { Properties.AnimationProperty }
                );
    }
}
