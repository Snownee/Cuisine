package snownee.cuisine.plugins;

import java.util.EnumSet;

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
import snownee.cuisine.internal.food.Drink;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.definition.ItemDefinition;

@KiwiModule(modid = Cuisine.MODID, name = ForestryCompat.MODID, dependency = ForestryCompat.MODID, optional = true)
public class ForestryCompat implements IModule
{
    static final String MODID = "forestry";

    @Override
    public void init()
    {
        Item material = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "crafting_material"));
        if (material != null)
        {
            Drink.Builder.FEATURE_INPUTS.put(ItemDefinition.of(material, 5), Drink.DrinkType.SMOOTHIE);
        }

        Material cherry = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("cherry", 0xb91023, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropCherry", cherry);
        Material walnut = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("walnut", 0xd88a44, 0, 0, 0, 0, 0, MaterialCategory.NUT).setValidForms(EnumSet.of(Form.MINCED)));
        CulinaryHub.API_INSTANCE.registerMapping("cropWalnut", walnut);
        Material chestnut = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("chestnut", 0x9e443b, 0, 0, 0, 0, 0, MaterialCategory.NUT).setValidForms(EnumSet.of(Form.MINCED, Form.PASTE)));
        CulinaryHub.API_INSTANCE.registerMapping("cropChestnut", chestnut);
        Material plum = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("plum", 0x4b213b, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPlum", plum);
        Material date = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("date", 0x4e302d, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropDate", date);
        Material papaya = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("papaya", 0xa99027, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
        CulinaryHub.API_INSTANCE.registerMapping("cropPapaya", papaya);

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
            Item log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.0"));
            Item planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "planks.0"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "larch"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 0)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "teak"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 1)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "desert_acacia"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 2)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "lime"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 3)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.1"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "chestnut"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 4)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "wenge"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 5)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "baobab"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 6)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "sequoia"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 7)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.2"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "kapok"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 8)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "ebony"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 9)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "mahogany"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 10)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "balsa"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 11)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.3"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "willow"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 12)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "walnut"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 13)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "greenheart"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 14)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "cherry"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 15)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.4"));
            planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "planks.1"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "mahoe"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 0)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "poplar"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 1)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "palm"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 2)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "papaya"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 3)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.5"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "pine"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 4)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "plum"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 5)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "maple"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 6)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "citrus"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 7)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.6"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "giant_sequoia"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 8)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "ipe"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 9)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "padauk"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 10)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "cocobolo"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 11)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.7"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "zebrawood"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 12)));
            }

            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.fireproof.0"));
            planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "planks.fireproof.0"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "larch_fp"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 0)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "teak_fp"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 1)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "desert_acacia_fp"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 2)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "lime_fp"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 3)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.fireproof.1"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "chestnut_fp"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 4)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "wenge_fp"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 5)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "baobab_fp"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 6)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "sequoia_fp"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 7)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.fireproof.2"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "kapok_fp"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 8)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "ebony_fp"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 9)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "mahogany_fp"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 10)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "balsa_fp"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 11)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.fireproof.3"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "willow_fp"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 12)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "walnut_fp"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 13)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "greenheart_fp"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 14)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "cherry_fp"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 15)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.fireproof.4"));
            planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "planks.fireproof.1"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "mahoe_fp"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 0)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "poplar_fp"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 1)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "palm_fp"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 2)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "papaya_fp"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 3)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.fireproof.5"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "pine_fp"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 4)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "plum_fp"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 5)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "maple_fp"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 6)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "citrus_fp"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 7)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.fireproof.6"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "giant_sequoia_fp"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 8)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "ipe_fp"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 9)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "padauk_fp"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 10)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "cocobolo_fp"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 11)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.fireproof.7"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "zebrawood_fp"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 12)));
            }

            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.vanilla.fireproof.0"));
            planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "planks.vanilla.fireproof.0"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "oak_fp"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 0)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "spruce_fp"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 1)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "birch_fp"), ItemDefinition.of(log, 2), new ItemStack(planks, amount, 2)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "jungle_fp"), ItemDefinition.of(log, 3), new ItemStack(planks, amount, 3)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "logs.vanilla.fireproof.1"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "acacia_fp"), ItemDefinition.of(log, 0), new ItemStack(planks, amount, 4)));
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "dark_oak_fp"), ItemDefinition.of(log, 1), new ItemStack(planks, amount, 5)));
            }
        }
    }
}
