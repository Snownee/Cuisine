package snownee.cuisine.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.Cuisine;

@Mod.EventBusSubscriber(modid = Cuisine.MODID)
public class CuisineFluids
{
    public static final Fluid SOY_MILK;
    public static final Fluid SOY_SAUCE;
    public static final Fluid RICE_VINEGAR;
    public static final Fluid FRUIT_VINEGAR;
    public static final Fluid SESAME_OIL;
    public static final Fluid EDIBLE_OIL;
    public static final Fluid JUICE;

    static
    {
        SOY_MILK = new VaporizableFluid("soy_milk").setDensity(1001);
        SOY_SAUCE = new Fluid("soy_sauce", new ResourceLocation(Cuisine.MODID, "block/soy_sauce_still"), new ResourceLocation(Cuisine.MODID, "block/soy_sauce_flow"));
        RICE_VINEGAR = new VaporizableFluid("rice_vinegar").setDensity(1001).setColor(0x77FFFFAA);
        FRUIT_VINEGAR = new VaporizableFluid("fruit_vinegar").setDensity(1001).setColor(0xEEFFFFFF);
        SESAME_OIL = new FluidOil("sesame_oil");
        EDIBLE_OIL = new FluidOil("edible_oil");
        JUICE = new FluidJuice("cuisine_juice");
    }

    @SubscribeEvent
    public static void registerFluids(RegistryEvent.Register<Block> event)
    {
        FluidRegistry.registerFluid(SOY_MILK);
        FluidRegistry.registerFluid(SOY_SAUCE);
        FluidRegistry.registerFluid(RICE_VINEGAR);
        FluidRegistry.registerFluid(FRUIT_VINEGAR);
        FluidRegistry.registerFluid(SESAME_OIL);
        FluidRegistry.registerFluid(EDIBLE_OIL);
        FluidRegistry.registerFluid(JUICE);
        FluidRegistry.addBucketForFluid(JUICE);

        BlockFluidSoyMilk soyMilkBlock = new BlockFluidSoyMilk(SOY_MILK);
        SOY_MILK.setBlock(soyMilkBlock);
        BlockFluid soySauceBlock = new BlockFluid(SOY_SAUCE, "soy_sauce", new MaterialLiquid(MapColor.BROWN));
        SOY_SAUCE.setBlock(soySauceBlock);
        BlockFluidVinegar riceVinegarBlock = new BlockFluidVinegar(RICE_VINEGAR, "rice_vinegar");
        RICE_VINEGAR.setBlock(riceVinegarBlock);
        BlockFluidVinegar fruitVinegarBlock = new BlockFluidVinegar(FRUIT_VINEGAR, "fruit_vinegar");
        FRUIT_VINEGAR.setBlock(fruitVinegarBlock);
        BlockFluidOil sesameOilBlock = new BlockFluidOil(SESAME_OIL, "sesame_oil");
        SESAME_OIL.setBlock(sesameOilBlock);
        BlockFluidOil edibleOilBlock = new BlockFluidOil(EDIBLE_OIL, "edible_oil");
        EDIBLE_OIL.setBlock(edibleOilBlock);
        event.getRegistry().registerAll(soyMilkBlock, soySauceBlock, riceVinegarBlock, fruitVinegarBlock, sesameOilBlock, edibleOilBlock);
    }
}
