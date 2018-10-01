package snownee.cuisine.blocks;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.tiles.TileBasin;
import snownee.kiwi.block.BlockMod;
import snownee.kiwi.util.InventoryUtil;

public class BlockBasin extends BlockMod
{

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
            if (entityIn.getClass() == EntityItem.class)
            {
                List<ItemStack> items = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.up())).stream().map(EntityItem::getItem).collect(Collectors.toList());
                for (ItemStack stack : InventoryUtil.mergeItemStacks(items, false))
                {
                    ((TileBasin) tile).process(Processing.BASIN_THROWING, stack);
                }
            }
            else if (fallDistance >= 1)
            {
                ItemStack input = ((TileBasin) tile).stacks.getStackInSlot(0);
                ((TileBasin) tile).process(Processing.SQUEEZING, input);
                if (entityIn instanceof EntityIronGolem)
                {
                    ((TileBasin) tile).process(Processing.SQUEEZING, input);
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
}
