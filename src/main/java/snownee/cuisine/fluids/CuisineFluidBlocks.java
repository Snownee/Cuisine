package snownee.cuisine.fluids;

import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.registry.GameRegistry;
import snownee.cuisine.Cuisine;

@GameRegistry.ObjectHolder(Cuisine.MODID)
public final class CuisineFluidBlocks
{
    private CuisineFluidBlocks()
    {
        throw new UnsupportedOperationException("No instance for you");
    }

    @GameRegistry.ObjectHolder("soy_milk")
    public static BlockFluidBase SOY_MILK;

    @GameRegistry.ObjectHolder("milk")
    public static BlockFluidBase MILK;

    @GameRegistry.ObjectHolder("soy_sauce")
    public static BlockFluidBase SOY_SAUCE;

    @GameRegistry.ObjectHolder("rice_vinegar")
    public static BlockFluidBase RICE_VINEGAR;

    @GameRegistry.ObjectHolder("fruit_vinegar")
    public static BlockFluidBase FRUIT_VINEGAR;

    @GameRegistry.ObjectHolder("sesame_oil")
    public static BlockFluidBase SESAME_OIL;

    @GameRegistry.ObjectHolder("edible_oil")
    public static BlockFluidBase EDIBLE_OIL;

    @GameRegistry.ObjectHolder("sugarcane_juice")
    public static BlockFluidBase SUGARCANE_JUICE;

}
