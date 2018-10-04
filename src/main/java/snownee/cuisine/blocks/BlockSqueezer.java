package snownee.cuisine.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.tiles.TileBasin;
import snownee.kiwi.block.BlockMod;

public class BlockSqueezer extends BlockMod
{

    public BlockSqueezer(String name)
    {
        super(name, Material.PISTON);
        setHardness(0.5F);
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    public boolean hasItem()
    {
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (pos.down().toLong() == fromPos.toLong())
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
        if (pre && !post)
        {
            worldIn.setBlockState(pos, state.withProperty(BlockDispenser.TRIGGERED, Boolean.valueOf(false)), 4);
        }
        else if (!pre && post)
        {
            worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
            worldIn.setBlockState(pos, state.withProperty(BlockDispenser.TRIGGERED, Boolean.valueOf(true)), 4);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tile = worldIn.getTileEntity(pos.down());
            if (tile instanceof TileBasin)
            {
                TileBasin tileBasin = (TileBasin) tile;
                tileBasin.process(Processing.SQUEEZING, tileBasin.stacks.getStackInSlot(0));
            }
        }
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
        return new BlockStateContainer(this, BlockDispenser.TRIGGERED);
    }
}
