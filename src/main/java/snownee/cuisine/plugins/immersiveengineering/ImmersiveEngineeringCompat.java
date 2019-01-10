package snownee.cuisine.plugins.immersiveengineering;

import blusunrize.immersiveengineering.api.tool.BelljarHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.process.Chopping;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.process.prefab.SimpleThrowing;
import snownee.cuisine.items.ItemCrops.Variant;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.definition.OreDictDefinition;

@KiwiModule(
        modid = Cuisine.MODID, name = ImmersiveEngineeringCompat.MODID, dependency = ImmersiveEngineeringCompat.MODID, optional = true
)
public class ImmersiveEngineeringCompat implements IModule
{
    static final String MODID = "immersiveengineering";

    @Override
    public void init()
    {
        if (CuisineConfig.GENERAL.axeChopping)
        {
            Item stick = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "material"));
            if (stick != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "treated_stick"), OreDictDefinition.of("plankTreatedWood"), new ItemStack(stick, CuisineConfig.GENERAL.axeChoppingStickOutput)));
            }
        }

        Fluid creosote = FluidRegistry.getFluid("creosote");
        Item treated_wood = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "treated_wood"));
        if (creosote != null && treated_wood != null)
        {
            Processing.BASIN_THROWING.add(new SimpleThrowing(new ResourceLocation(MODID, "treated_wood"), OreDictDefinition.of("plankWood"), new FluidStack(creosote, 125), new ItemStack(treated_wood)));
        }

        for (Variant variant : CuisineRegistry.CROPS.getVariants())
        {
            BelljarHandler.registerHandler(new CuisinePlantHandler(variant));
        }

    }
}
