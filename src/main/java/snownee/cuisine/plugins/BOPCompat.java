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

@KiwiModule(modid = Cuisine.MODID, name = "biomesoplenty", dependency = "biomesoplenty", optional = true)
public class BOPCompat implements IModule
{
    @Override
    public void init()
    {
        Material pear = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("pear", 0xA3B26D, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPear", pear);
        Material peach = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("peach", 0xFEB5A0, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPeach", peach);
        Material persimmon = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("persimmon", 0xFF9528, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPersimmon", persimmon);
        if (CuisineConfig.GENERAL.axeChopping)
        {
            Item itemBerry = ForgeRegistries.ITEMS.getValue(new ResourceLocation("biomesoplenty", "berries"));
            if (itemBerry != null)
            {
                Material wildberry = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("wildberry", 0x4D1F23, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
                CulinaryHub.API_INSTANCE.registerMapping(ItemDefinition.of(itemBerry), wildberry);
            }
            Item itemKelp = ForgeRegistries.ITEMS.getValue(new ResourceLocation("biomesoplenty", "seaweed"));
            if (itemKelp != null)
            {
                OreDictionary.registerOre("cropSeaweed", new ItemStack(itemKelp));
                Material seaweed = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("seaweed", 0x99909648, 0, 0, 0, 0, 0, MaterialCategory.SEAFOOD).setValidForms(Form.JUICE_ONLY));
                CulinaryHub.API_INSTANCE.registerMapping("cropSeaweed", seaweed);
            }
            Item itemBambooshoot = ForgeRegistries.ITEMS.getValue(new ResourceLocation("biomesoplenty", "sapling_0"));
            if (itemBambooshoot != null)
            {
                OreDictionary.registerOre("cropBambooshoot", new ItemStack(itemBambooshoot, 1, 2));
            }

            Item log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("biomesoplenty", "log_0"));
            Item planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation("biomesoplenty", "planks_0"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 4), new ItemStack(planks, 6, 0)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 5), new ItemStack(planks, 6, 1)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 6), new ItemStack(planks, 6, 2)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 7), new ItemStack(planks, 6, 3)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("biomesoplenty", "log_1"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 4), new ItemStack(planks, 6, 4)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 5), new ItemStack(planks, 6, 5)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 6), new ItemStack(planks, 6, 6)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 7), new ItemStack(planks, 6, 7)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("biomesoplenty", "log_2"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 4), new ItemStack(planks, 6, 8)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 5), new ItemStack(planks, 6, 9)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 6), new ItemStack(planks, 6, 10)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 7), new ItemStack(planks, 6, 11)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("biomesoplenty", "log_3"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 4), new ItemStack(planks, 6, 12)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 5), new ItemStack(planks, 6, 13)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 6), new ItemStack(planks, 6, 14)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 7), new ItemStack(planks, 6, 15)));
            }
        }
    }
}
