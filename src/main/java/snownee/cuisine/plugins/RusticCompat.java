package snownee.cuisine.plugins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.prefab.SimpleMaterialImpl;
import snownee.cuisine.api.process.Chopping;
import snownee.cuisine.api.process.Processing;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.definition.ItemDefinition;

@KiwiModule(modid = Cuisine.MODID, name = RusticCompat.MODID, dependency = RusticCompat.MODID, optional = true)
public class RusticCompat implements IModule
{
    static final String MODID = "rustic";

    @Override
    public void init()
    {
        Material grape = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("grape", 0x582945, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropGrape", grape);
        Material ironberry = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("ironberry", 0x424242, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropIronberry", ironberry);
        Material wildberry = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("wildberry", 0x4D1F23, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropWildberry", wildberry);

        if (CuisineConfig.GENERAL.axeChopping)
        {
            Item log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "log"));
            Item planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "planks"));
            if (log != null && planks != null)
            {
                int amount = CuisineConfig.GENERAL.axeChoppingPlanksOutput;
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "olive"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 0)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "ironwood"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 1)));
            }
        }
    }
}
