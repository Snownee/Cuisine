package snownee.cuisine.plugins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
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

@KiwiModule(modid = Cuisine.MODID, name = BOPCompat.MODID, dependency = BOPCompat.MODID, optional = true)
public class BOPCompat implements IModule
{
    static final String MODID = "biomesoplenty";

    @Override
    public void init()
    {
        Material pear = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("pear", 0xA3B26D, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPear", pear);
        Material peach = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("peach", 0xFEB5A0, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPeach", peach);
        Material persimmon = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("persimmon", 0xFF9528, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPersimmon", persimmon);
        Item itemBerry = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "berries"));
        if (itemBerry != null)
        {
            Material wildberry = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("wildberry", 0x4D1F23, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
            CulinaryHub.API_INSTANCE.registerMapping(ItemDefinition.of(itemBerry), wildberry);
        }
        Item itemKelp = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "seaweed"));
        if (itemKelp != null)
        {
            OreDictionary.registerOre("cropSeaweed", new ItemStack(itemKelp));
            Material seaweed = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("seaweed", 0x99909648, 0, 0, 0, 0, 0, MaterialCategory.SEAFOOD).setValidForms(Form.JUICE_ONLY));
            CulinaryHub.API_INSTANCE.registerMapping("cropSeaweed", seaweed);
        }
        Item itemBambooshoot = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "sapling_0"));
        if (itemBambooshoot != null)
        {
            OreDictionary.registerOre("cropBambooshoot", new ItemStack(itemBambooshoot, 1, 2));
        }

        if (CuisineConfig.GENERAL.axeChopping)
        {
            int amount = CuisineConfig.GENERAL.axeChoppingPlanksOutput;
            Item log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "log_0"));
            Item planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "planks_0"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "sacred_oak"), ItemDefinition.of(log, 4), new ItemStack(planks, amount, 0)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "cherry"), ItemDefinition.of(log, 5), new ItemStack(planks, amount, 1)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "umbran"), ItemDefinition.of(log, 6), new ItemStack(planks, amount, 2)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "fir"), ItemDefinition.of(log, 7), new ItemStack(planks, amount, 3)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "log_1"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "ethereal"), ItemDefinition.of(log, 4), new ItemStack(planks, amount, 4)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "magic"), ItemDefinition.of(log, 5), new ItemStack(planks, amount, 5)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "mangrove"), ItemDefinition.of(log, 6), new ItemStack(planks, amount, 6)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "palm"), ItemDefinition.of(log, 7), new ItemStack(planks, amount, 7)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "log_2"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "redwood"), ItemDefinition.of(log, 4), new ItemStack(planks, amount, 8)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "willow"), ItemDefinition.of(log, 5), new ItemStack(planks, amount, 9)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "pine"), ItemDefinition.of(log, 6), new ItemStack(planks, amount, 10)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "hellbark"), ItemDefinition.of(log, 7), new ItemStack(planks, amount, 11)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "log_3"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "jacaranda"), ItemDefinition.of(log, 4), new ItemStack(planks, amount, 12)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "mahogany"), ItemDefinition.of(log, 5), new ItemStack(planks, amount, 13)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "ebony"), ItemDefinition.of(log, 6), new ItemStack(planks, amount, 14)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "eucalyptus"), ItemDefinition.of(log, 7), new ItemStack(planks, amount, 15)));
            }
        }
    }
}
