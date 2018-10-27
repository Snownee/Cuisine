package snownee.cuisine.plugins;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.definition.ItemDefinition;

@KiwiModule(modid = Cuisine.MODID, name = "vanillafoodpantry", dependency = "vanillafoodpantry", optional = true)
public class VFPCompat implements IModule
{

    private static final ResourceLocation VFP_JUICE_ID = new ResourceLocation("vanillafoodpantry", "juice");

    @Override
    public void init()
    {
        Item juice = ForgeRegistries.ITEMS.getValue(VFP_JUICE_ID);
        if (juice != null)
        {
            juice.setContainerItem(Items.GLASS_BOTTLE);
            // Oh no! look at these magic numbers! and VFP is not open source!
            CulinaryHub.API_INSTANCE.registerMapping(ItemDefinition.of(juice, 201), new Ingredient(CulinaryHub.CommonMaterials.APPLE, Form.JUICE, 1));
            CulinaryHub.API_INSTANCE.registerMapping(ItemDefinition.of(juice, 202), new Ingredient(CulinaryHub.CommonMaterials.CARROT, Form.JUICE, 1));
            CulinaryHub.API_INSTANCE.registerMapping(ItemDefinition.of(juice, 204), new Ingredient(CulinaryHub.CommonMaterials.CACTUS, Form.JUICE, 1));
        }
    }

    @Override
    public void postInit()
    {
        Item juice = ForgeRegistries.ITEMS.getValue(VFP_JUICE_ID);
        if (juice != null)
        {
            // Sad truth: we cannot ensure grape material are registered before VFPCompat#init
            Material grape = CulinaryHub.API_INSTANCE.findMaterial("grape");
            if (grape != null)
            {
                CulinaryHub.API_INSTANCE.registerMapping(ItemDefinition.of(juice, 212), new Ingredient(CulinaryHub.CommonMaterials.APPLE, Form.JUICE, 1));
            }
        }
    }
}
