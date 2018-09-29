package snownee.cuisine.world.gen.feature;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockBambooPlant;
import snownee.cuisine.blocks.BlockBambooPlant.Type;

public class WorldFeatureBamboo extends WorldGenAbstractTree
{

    public WorldFeatureBamboo(boolean notify)
    {
        super(notify);
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos pos)
    {
        BlockPos down = pos.down();
        IBlockState stateSoil = worldIn.getBlockState(down);
        boolean isSoil = stateSoil.getBlock().canSustainPlant(stateSoil, worldIn, down, EnumFacing.UP, CuisineRegistry.BAMBOO_PLANT);
        if (!isSoil)
        {
            return false;
        }

        int height = 7 + rand.nextInt(4);
        for (int i = height; i-- > 1;)
        {
            BlockPos pos2 = pos.up(i);
            if (worldIn.isOutsideBuildHeight(pos2) || !worldIn.isAirBlock(pos2))
            {
                return false;
            }
        }
        IBlockState newState = CuisineRegistry.BAMBOO_PLANT.getDefaultState().withProperty(BlockBambooPlant.TYPE, Type.A_2);
        for (int i = height; i-- > 0;)
        {
            setBlockAndNotifyAdequately(worldIn, pos.up(i), newState);
        }
        return true;
    }
}
