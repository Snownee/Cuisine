package snownee.cuisine.world.gen;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeSwamp;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockModSapling.Type;
import snownee.cuisine.world.feature.WorldFeatureCitrusGenusTree;

public class WorldGenCitrusTrees
{
    private static final Type[] TYPES = new Type[] { Type.CITRON, Type.CITRON, Type.CITRON, Type.LIME, Type.LIME, Type.LIME, Type.MANDARIN, Type.MANDARIN, Type.MANDARIN, Type.LIME, Type.LIME, Type.LEMON, Type.LEMON, Type.ORANGE, Type.ORANGE, Type.GRAPEFRUIT };

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public void decorateEvent(Decorate event)
    {
        if (event.getType() == Decorate.EventType.TREE && event.getRand().nextInt(200) < CuisineConfig.WORLD_GEN.fruitTreesGenRate)
        {
            Random rand = event.getRand();
            World world = event.getWorld();
            BlockPos pos = event.getChunkPos().getBlock(rand.nextInt(16) + 8, 0, rand.nextInt(16) + 8);
            Biome biome = world.getBiome(pos);
            if (!biome.canRain() || biome.isSnowyBiome() || biome.decorator.treesPerChunk < 2 || biome.decorator.treesPerChunk > 10 || biome instanceof BiomeSwamp || rand.nextDouble() > biome.getDefaultTemperature())
            {
                return;
            }
            pos = WorldGenHelper.findGround(world, pos, false);
            if (pos != null && pos.getY() < 100 && CuisineRegistry.SAPLING.canPlaceBlockAt(world, pos))
            {
                Type type = TYPES[rand.nextInt(TYPES.length)];
                WorldGenerator generator = new WorldFeatureCitrusGenusTree(false, type, true);
                generator.generate(world, rand, pos);
            }
        }
    }
}
