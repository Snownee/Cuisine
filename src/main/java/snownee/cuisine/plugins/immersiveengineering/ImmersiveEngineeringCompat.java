package snownee.cuisine.plugins.immersiveengineering;

import blusunrize.immersiveengineering.api.tool.BelljarHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.process.Chopping;
import snownee.cuisine.api.process.Processing;
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

        for (Variant variant : CuisineRegistry.CROPS.getVariants())
        {
            Variant variantCasted = variant;
            BelljarHandler.registerHandler(new CuisinePlantHandler(variantCasted));
        }

    }
}
