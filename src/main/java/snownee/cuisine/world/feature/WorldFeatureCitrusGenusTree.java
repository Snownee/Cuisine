package snownee.cuisine.world.feature;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockModLeaves;
import snownee.cuisine.blocks.BlockModSapling;
import snownee.cuisine.tiles.TileFruitTree;

public class WorldFeatureCitrusGenusTree extends WorldGenAbstractTree
{
    private final BlockModSapling.Type type;
    private final boolean flower;

    /**
     *
     * @param notifyUpdate true if setting new block will cause block update
     * @param type the leaf type token
     */
    public WorldFeatureCitrusGenusTree(boolean notifyUpdate, BlockModSapling.Type type, boolean flower)
    {
        super(notifyUpdate);
        this.type = type;
        this.flower = flower;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        final int i = 5 + rand.nextInt(2);
        boolean flag = true;

        if (position.getY() < 1 || position.getY() + i + 1 > worldIn.getHeight())
        {
            return false;
        }
        for (int j = position.getY(); j <= position.getY() + 1 + i; ++j)
        {
            int k = 1;

            if (j == position.getY())
            {
                k = 0;
            }

            if (j >= position.getY() + 1 + i - 2)
            {
                k = 2;
            }

            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l)
            {
                for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1)
                {
                    if (j >= 0 && j < worldIn.getHeight())
                    {
                        if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.setPos(l, j, i1)))
                        {
                            flag = false;
                        }
                    }
                    else
                    {
                        flag = false;
                    }
                }
            }
        }

        if (!flag)
        {
            return false;
        }
        else
        {
            IBlockState state = worldIn.getBlockState(position.down());

            if (state.getBlock().canSustainPlant(state, worldIn, position.down(), net.minecraft.util.EnumFacing.UP, CuisineRegistry.SAPLING) && position.getY() < worldIn.getHeight() - i - 1)
            {
                IBlockState wood = CuisineRegistry.LOG.getDefaultState();
                IBlockState leaves = getLeavesFromType(type);

                state.getBlock().onPlantGrow(state, worldIn, position.down(), position);
                for (int i3 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3)
                {
                    int i4 = i3 - (position.getY() + i);
                    int j1 = 1 - i4 / 2;

                    for (int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1)
                    {
                        int l1 = k1 - position.getX();

                        for (int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2)
                        {
                            int j2 = i2 - position.getZ();

                            if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextInt(2) != 0 && i4 != 0)
                            {
                                BlockPos blockpos = new BlockPos(k1, i3, i2);
                                state = worldIn.getBlockState(blockpos);

                                if (state.getBlock().isAir(state, worldIn, blockpos) || state.getBlock().isLeaves(state, worldIn, blockpos) || state.getMaterial() == Material.VINE)
                                {
                                    if (flower && rand.nextInt(5) == 0)
                                    {
                                        this.setBlockAndNotifyAdequately(worldIn, blockpos, leaves.withProperty(BlockModLeaves.AGE, 2));
                                    }
                                    else
                                    {
                                        this.setBlockAndNotifyAdequately(worldIn, blockpos, leaves);
                                    }
                                }
                            }
                        }
                    }
                }

                for (int j3 = 0; j3 < i; ++j3)
                {
                    BlockPos upN = position.up(j3);
                    state = worldIn.getBlockState(upN);

                    if (state.getBlock().isAir(state, worldIn, upN) || state.getBlock().isLeaves(state, worldIn, upN) || state.getMaterial() == Material.VINE)
                    {
                        this.setBlockAndNotifyAdequately(worldIn, upN, wood);
                    }
                }

                BlockPos upI = position.up(i);
                this.setBlockAndNotifyAdequately(worldIn, upI, leaves.withProperty(BlockModLeaves.CORE, true));
                TileEntity tile = worldIn.getTileEntity(upI);
                if (tile instanceof TileFruitTree)
                {
                    ((TileFruitTree) tile).type = this.type;
                }

                return true;
            }
            else
            {
                return false;
            }
        }
    }

    private static IBlockState getLeavesFromType(BlockModSapling.Type type)
    {
        switch (type)
        {
        case CITRON:
            return CuisineRegistry.LEAVES_CITRON.getDefaultState();
        case GRAPEFRUIT:
            return CuisineRegistry.LEAVES_GRAPEFRUIT.getDefaultState();
        case LEMON:
            return CuisineRegistry.LEAVES_LEMON.getDefaultState();
        case LIME:
            return CuisineRegistry.LEAVES_LIME.getDefaultState();
        case MANDARIN:
            return CuisineRegistry.LEAVES_MANDARIN.getDefaultState();
        case ORANGE:
            return CuisineRegistry.LEAVES_ORANGE.getDefaultState();
        default:
            return CuisineRegistry.LEAVES_POMELO.getDefaultState();
        }
    }
}
