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
import snownee.cuisine.items.ItemBasicFood.Variants.SubItem;
import snownee.cuisine.items.ItemCrops.Variants.SubCrop;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.VariantsHolder.Variant;
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
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "treated_stick"), OreDictDefinition.of("plankTreatedWood"), new ItemStack(stick, 4)));
            }
        }

        // I have to say design of Kiwi is a failure.
        for (Variant<? extends SubItem> variant : CuisineRegistry.CROPS.getVariants())
        {
            Variant<SubCrop> variantCasted = (Variant<SubCrop>) variant;
            BelljarHandler.registerHandler(new CuisinePlantHandler(variantCasted));
        }

    }
}
