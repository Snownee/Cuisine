package snownee.cuisine.blocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.GameRegistry;
import snownee.cuisine.Cuisine;

@GameRegistry.ObjectHolder(Cuisine.MODID)
public class CuisineBlocks
{
    // FLUIDS
    @GameRegistry.ObjectHolder("soy_milk")
    public static final Block SOY_MILK = Blocks.AIR;

    @GameRegistry.ObjectHolder("soy_sauce")
    public static final Block SOY_SAUCE = Blocks.AIR;

    @GameRegistry.ObjectHolder("rice_vinegar")
    public static final Block RICE_VINEGAR = Blocks.AIR;

    @GameRegistry.ObjectHolder("fruit_vinegar")
    public static final Block FRUIT_VINEGAR = Blocks.AIR;

    @GameRegistry.ObjectHolder("sesame_oil")
    public static final Block SESAME_OIL = Blocks.AIR;

    @GameRegistry.ObjectHolder("edible_oil")
    public static final Block EDIBLE_OIL = Blocks.AIR;

    public static final Block BEET_JUICE = Blocks.AIR;

    public static final Block SUGARCANE_JUICE = Blocks.AIR;

}
