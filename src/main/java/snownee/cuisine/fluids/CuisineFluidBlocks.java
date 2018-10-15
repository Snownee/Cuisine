package snownee.cuisine.fluids;

import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.registry.GameRegistry;
import snownee.cuisine.Cuisine;

@GameRegistry.ObjectHolder(Cuisine.MODID)
public class CuisineFluidBlocks
{

    @GameRegistry.ObjectHolder("soy_milk")
    public static final BlockFluidBase SOY_MILK;

    @GameRegistry.ObjectHolder("milk")
    public static final BlockFluidBase MILK;

    @GameRegistry.ObjectHolder("soy_sauce")
    public static final BlockFluidBase SOY_SAUCE;

    @GameRegistry.ObjectHolder("rice_vinegar")
    public static final BlockFluidBase RICE_VINEGAR;

    @GameRegistry.ObjectHolder("fruit_vinegar")
    public static final BlockFluidBase FRUIT_VINEGAR;

    @GameRegistry.ObjectHolder("sesame_oil")
    public static final BlockFluidBase SESAME_OIL;

    @GameRegistry.ObjectHolder("edible_oil")
    public static final BlockFluidBase EDIBLE_OIL;

    @GameRegistry.ObjectHolder("sugarcane_juice")
    public static final BlockFluidBase SUGARCANE_JUICE;

    static
    {
        SOY_MILK = null;
        MILK = null;
        SOY_SAUCE = null;
        RICE_VINEGAR = null;
        FRUIT_VINEGAR = null;
        SESAME_OIL = null;
        EDIBLE_OIL = null;
        SUGARCANE_JUICE = null;
    }

}
