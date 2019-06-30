package snownee.cuisine.world.gen;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockCuisineCrops;

public class WorldGenGarden
{
    public static final Block[] PLANT_POOL = new Block[] { CuisineRegistry.CHINESE_CABBAGE, CuisineRegistry.CORN, CuisineRegistry.CUCUMBER, CuisineRegistry.EGGPLANT, CuisineRegistry.GINGER, CuisineRegistry.GREEN_PEPPER, CuisineRegistry.LEEK, CuisineRegistry.LETTUCE, CuisineRegistry.ONION, CuisineRegistry.PEANUT, CuisineRegistry.RED_PEPPER, CuisineRegistry.SCALLION, CuisineRegistry.SESAME, CuisineRegistry.SICHUAN_PEPPER, CuisineRegistry.SOYBEAN, CuisineRegistry.SPINACH, CuisineRegistry.TOMATO, CuisineRegistry.TURNIP, Blocks.CARROTS, Blocks.POTATOES, Blocks.WHEAT };

    @SubscribeEvent
    public void decorateEvent(Decorate event)
    {
        World worldIn = event.getWorld();
        if (event.getType() == Decorate.EventType.PUMPKIN && Arrays.binarySearch(CuisineConfig.WORLD_GEN.cropsGenDimensions, worldIn.provider.getDimension()) >= 0)
        {
            Random rand = event.getRand();
            BlockPos position = event.getChunkPos().getBlock(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8);

            Biome biome = worldIn.getBiome(position);

            int flowers = biome.decorator.flowersPerChunk;
            if (flowers == -999 && biome.getClass().getName().startsWith("biomesoplenty"))
            {
                flowers = 1;
            }
            if (!biome.canRain() || biome.isSnowyBiome() || biome.decorator.flowersPerChunk < 1 || biome instanceof BiomeOcean || rand.nextDouble() > biome.getDefaultTemperature() || rand.nextInt(200) >= CuisineConfig.WORLD_GEN.cropsGenRate)
            {
                return;
            }

            BlockPos.MutableBlockPos pos = WorldGenHelper.findGround(worldIn, position, true, true, true);
            if (pos == null)
            {
                return;
            }
            pos.move(EnumFacing.DOWN);

            IBlockState state = worldIn.getBlockState(pos);
            if (biome.topBlock.getMaterial() == Material.GRASS && state.getBlock() == biome.topBlock.getBlock())
            {
                Block plant = PLANT_POOL[rand.nextInt(PLANT_POOL.length)];
                plant(worldIn, pos, plant, biome.topBlock.getBlock(), rand);
                plant(worldIn, pos.offset(EnumFacing.byHorizontalIndex(rand.nextInt(4))), plant, biome.topBlock.getBlock(), rand);
                plant(worldIn, pos.offset(EnumFacing.byHorizontalIndex(rand.nextInt(4))), plant, biome.topBlock.getBlock(), rand);
            }
            else if (state.getBlock() == Blocks.WATER)
            {
                state = worldIn.getBlockState(pos.down());
                if (state.getMaterial() == Material.GROUND || state.getMaterial() == Material.GRASS)
                {
                    pos.move(EnumFacing.UP);
                    worldIn.setBlockState(pos, CuisineRegistry.RICE.withAge(rand.nextInt(CuisineRegistry.RICE.getMaxAge())), 0);
                }
            }
        }
        else if (worldIn.provider.getDimension() == -1)
        {
            Random rand = event.getRand();
            if (rand.nextInt(4) > 0 || rand.nextInt(100) < CuisineConfig.WORLD_GEN.cropsGenRate)
            {
                return;
            }
            BlockPos position = event.getChunkPos().getBlock(rand.nextInt(16) + 8, rand.nextInt(50) + 33, rand.nextInt(16) + 8);
            BlockPos.MutableBlockPos pos = WorldGenHelper.findGround(worldIn, position, true, true, false, 20);
            if (pos == null)
            {
                return;
            }
            pos.move(EnumFacing.DOWN);
            if (worldIn.getBlockState(pos).getBlock() == Blocks.SOUL_SAND)
            {
                plant(worldIn, pos, CuisineRegistry.CHILI, Blocks.SOUL_SAND, rand);
                //plant(worldIn, pos.offset(EnumFacing.byHorizontalIndex(rand.nextInt(4))), CuisineRegistry.CHILI, Blocks.SOUL_SAND, rand);
            }
        }
    }

    private static void plant(World world, BlockPos pos, Block block, Block replacedBlock, Random rand)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == replacedBlock)
        {
            if (block instanceof BlockCuisineCrops)
            {
                BlockCuisineCrops blockCuisineCrops = (BlockCuisineCrops) block;
                if (blockCuisineCrops.getPlantType(world, pos) == EnumPlantType.Crop)
                {
                    world.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 0);
                }
                pos = pos.up();
                if (world.getBlockState(pos).getBlock().isReplaceable(world, pos))
                {
                    world.setBlockState(pos, blockCuisineCrops.withAge(block == CuisineRegistry.CORN ? 0 : rand.nextInt(blockCuisineCrops.getMaxAge())), 0);
                }
            }
            else if (block instanceof BlockCrops)
            {
                world.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 0);
                BlockCrops blockCrops = (BlockCrops) block;
                pos = pos.up();
                if (world.getBlockState(pos).getBlock().isReplaceable(world, pos))
                {
                    world.setBlockState(pos, blockCrops.withAge(rand.nextInt(blockCrops.getMaxAge())), 0);
                }
            }
        }
    }

}
