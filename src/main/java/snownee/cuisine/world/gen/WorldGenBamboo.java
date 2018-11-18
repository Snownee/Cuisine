package snownee.cuisine.world.gen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.biome.BiomeSwamp;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.world.feature.WorldFeatureBamboo;

public class WorldGenBamboo
{

    @SubscribeEvent(priority = EventPriority.LOW)
    public void decorateEvent(Decorate event)
    {
        World worldIn = event.getWorld();
        if (worldIn.provider.getDimension() == 0 && event.getType() == Decorate.EventType.TREE)
        {
            Random rand = event.getRand();
            BlockPos position = event.getChunkPos().getBlock(rand.nextInt(16) + 8, 128, rand.nextInt(16) + 8);

            Biome biome = worldIn.getBiome(position);

            if (biome.getBaseHeight() > 0.4F || biome.isSnowyBiome() || biome.getRainfall() < 0.5F || biome instanceof BiomeOcean || biome instanceof BiomeSwamp || rand.nextInt(CuisineConfig.WORLD_GEN.bamboosGenRate) > 0 || rand.nextDouble() > biome.getDefaultTemperature())
            {
                return;
            }

            position = WorldGenHelper.findGround(worldIn, position, true);
            if (position == null)
            {
                return;
            }

            int count = 15 + rand.nextInt(10);
            WorldFeatureBamboo generator = new WorldFeatureBamboo(false);
            while (--count != 0)
            {
                BlockPos pos = position.add(rand.nextInt(13) - 6, 3, rand.nextInt(13) - 6);
                for (IBlockState iblockstate = worldIn.getBlockState(pos); iblockstate.getBlock().isAir(iblockstate, worldIn, pos) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, pos); iblockstate = worldIn.getBlockState(pos))
                {
                    if (pos.getY() + 3 < position.getY())
                    {
                        break;
                    }
                    pos = pos.down();
                }
                pos = pos.up();

                if (count > 7)
                {
                    generator.generate(worldIn, rand, pos);
                }
                else if (CuisineRegistry.BAMBOO_PLANT.canPlaceBlockAt(worldIn, pos))
                {
                    worldIn.setBlockState(pos, CuisineRegistry.BAMBOO_PLANT.getDefaultState());
                }
            }

        }
    }
}
