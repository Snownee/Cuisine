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

@KiwiModule(modid = Cuisine.MODID, name = "forestry", dependency = "forestry", optional = true)
public class ForestryCompat implements IModule
{
    @Override
    public void init()
    {
        Item material = Item.getByNameOrId("forestry:crafting_material");
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

        Item itemBerry = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "berries"));
        if (itemBerry != null)
        {
            Material wildberry = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("wildberry", 0x4D1F23, 0, 0, 0, 0, 0, MaterialCategory.FRUIT).setValidForms(Form.JUICE_ONLY));
            CulinaryHub.API_INSTANCE.registerMapping(ItemDefinition.of(itemBerry), wildberry);
        }
        Item itemKelp = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "seaweed"));
        if (itemKelp != null)
        {
            OreDictionary.registerOre("cropSeaweed", new ItemStack(itemKelp));
            Material seaweed = CulinaryHub.API_INSTANCE.register(new SimpleMaterialImpl("seaweed", 0x99909648, 0, 0, 0, 0, 0, MaterialCategory.SEAFOOD).setValidForms(Form.JUICE_ONLY));
            CulinaryHub.API_INSTANCE.registerMapping("cropSeaweed", seaweed);
        }
        Item itemBambooshoot = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "sapling_0"));
        if (itemBambooshoot != null)
        {
            OreDictionary.registerOre("cropBambooshoot", new ItemStack(itemBambooshoot, 1, 2));
        }

        if (CuisineConfig.GENERAL.axeChopping)
        {
            Item log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.0"));
            Item planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "planks.0"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 0)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 1)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 2)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 3)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.1"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 4)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 5)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 6)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 7)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.2"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 8)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 9)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 10)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 11)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.3"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 12)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 13)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 14)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 15)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.4"));
            planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "planks.1"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 0)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 1)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 2)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 3)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.5"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 4)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 5)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 6)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 7)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.6"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 8)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 9)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 10)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 11)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.7"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 12)));
            }

            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.fireproof.0"));
            planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "planks.fireproof.0"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 0)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 1)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 2)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 3)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.fireproof.1"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 4)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 5)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 6)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 7)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.fireproof.2"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 8)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 9)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 10)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 11)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.fireproof.3"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 12)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 13)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 14)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 15)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.fireproof.4"));
            planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "planks.fireproof.1"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 0)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 1)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 2)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 3)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.fireproof.5"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 4)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 5)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 6)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 7)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.fireproof.6"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 8)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 9)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 10)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 11)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.fireproof.7"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 12)));
            }

            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.vanilla.fireproof.0"));
            planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "planks.vanilla.fireproof.0"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 0)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 1)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 2), new ItemStack(planks, 6, 2)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 3), new ItemStack(planks, 6, 3)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation("forestry", "logs.vanilla.fireproof.1"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 0), new ItemStack(planks, 6, 4)));
                Processing.CHOPPING.add(new Chopping(ItemDefinition.of(log, 1), new ItemStack(planks, 6, 5)));
            }
        }
    }
}
