package snownee.cuisine.events;

import java.util.function.Consumer;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.items.ItemCrops;
import snownee.cuisine.items.ItemMortar;
import snownee.kiwi.util.VariantsHolder.Variant;

public class OreDictHandler
{
    public static void init()
    {
        // NUT
        OreDictionary.registerOre("cropPeanut", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.PEANUT));

        // GRAIN
        OreDictionary.registerOre("cropWheat", Items.WHEAT);
        OreDictionary.registerOre("listAllgrain", Items.WHEAT);
        OreDictionary.registerOre("cropSesame", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.SESAME));
        OreDictionary.registerOre("cropBrownrice", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.RICE));
        OreDictionary.registerOre("cropSoybean", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.SOYBEAN));
        OreDictionary.registerOre("cropCorn", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.CORN));
        OreDictionary.registerOre("cropRice", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.WHITE_RICE));

        // VEGGIE
        OreDictionary.registerOre("cropBeetroot", Items.BEETROOT);
        OreDictionary.registerOre("listAllveggie", Items.BEETROOT);
        OreDictionary.registerOre("cropPotato", Items.POTATO);
        OreDictionary.registerOre("listAllveggie", Items.POTATO);
        OreDictionary.registerOre("cropCarrot", Items.CARROT);
        OreDictionary.registerOre("listAllveggie", Items.CARROT);
        OreDictionary.registerOre("cropPumpkin", Blocks.PUMPKIN);
        OreDictionary.registerOre("listAllveggie", Blocks.PUMPKIN);
        OreDictionary.registerOre("cropBrownmushroom", Blocks.BROWN_MUSHROOM);
        OreDictionary.registerOre("cropRedmushroom", Blocks.RED_MUSHROOM);
        OreDictionary.registerOre("cropTomato", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.TOMATO));
        OreDictionary.registerOre("cropChilipepper", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.CHILI));
        OreDictionary.registerOre("cropGarlic", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.GARLIC));
        OreDictionary.registerOre("cropGinger", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.GINGER));
        OreDictionary.registerOre("cropScallion", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.SCALLION));
        OreDictionary.registerOre("cropTurnip", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.TURNIP));
        OreDictionary.registerOre("cropCabbage", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.CHINESE_CABBAGE));
        OreDictionary.registerOre("cropLettuce", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.LETTUCE));
        OreDictionary.registerOre("cropCucumber", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.CUCUMBER));
        OreDictionary.registerOre("cropBellpepper", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.RED_PEPPER));
        OreDictionary.registerOre("cropBellpepper", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.GREEN_PEPPER));
        OreDictionary.registerOre("cropLeek", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.LEEK));
        OreDictionary.registerOre("cropOnion", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.ONION));
        OreDictionary.registerOre("cropEggplant", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.EGGPLANT));
        OreDictionary.registerOre("cropSpinach", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.SPINACH));

        // SPICE
        OreDictionary.registerOre("dustSalt", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.SALT));
        OreDictionary.registerOre("listAllspice", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.SALT));
        OreDictionary.registerOre("dustCrudesalt", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.CRUDE_SALT));
        OreDictionary.registerOre("listAllspice", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.CRUDE_SALT));
        OreDictionary.registerOre("listAllsugar", Items.SUGAR);
        OreDictionary.registerOre("listAllspice", Items.SUGAR);
        OreDictionary.registerOre("cropSichuanpepper", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.SICHUAN_PEPPER));
        OreDictionary.registerOre("listAllspice", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.SICHUAN_PEPPER));

        // MEAT
        OreDictionary.registerOre("listAllbeefraw", Items.BEEF);
        OreDictionary.registerOre("listAllmeatraw", Items.BEEF);
        OreDictionary.registerOre("listAllporkraw", Items.PORKCHOP);
        OreDictionary.registerOre("listAllmeatraw", Items.PORKCHOP);
        OreDictionary.registerOre("listAllchickenraw", Items.CHICKEN);
        OreDictionary.registerOre("listAllmeatraw", Items.CHICKEN);
        OreDictionary.registerOre("listAllrabbitraw", Items.RABBIT);
        OreDictionary.registerOre("listAllmeatraw", Items.RABBIT);
        OreDictionary.registerOre("listAllmuttonraw", Items.MUTTON);
        OreDictionary.registerOre("listAllmeatraw", Items.MUTTON);

        // FRUIT
        OreDictionary.registerOre("cropApple", Items.APPLE);
        OreDictionary.registerOre("listAllfruit", Items.APPLE);
        OreDictionary.registerOre("cropMelon", Items.MELON);
        OreDictionary.registerOre("listAllfruit", Items.MELON);
        OreDictionary.registerOre("cropChorusfruit", Items.CHORUS_FRUIT);
        OreDictionary.registerOre("listAllfruit", Items.CHORUS_FRUIT);
        OreDictionary.registerOre("cropMandarin", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.MANDARIN));
        OreDictionary.registerOre("cropCitron", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.CITRON));
        OreDictionary.registerOre("cropPomelo", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.POMELO));
        OreDictionary.registerOre("cropOrange", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.ORANGE));
        OreDictionary.registerOre("cropLemon", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.LEMON));
        OreDictionary.registerOre("cropLime", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.LIME));
        OreDictionary.registerOre("cropGrapefruit", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.GRAPEFRUIT));

        // MISC
        OreDictionary.registerOre("foodFirmtofu", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.TOFU));
        OreDictionary.registerOre("foodWhiterice", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.WHITE_RICE));
        OreDictionary.registerOre("listAllwater", CuisineRegistry.ITEM_MORTAR.getItemStack(ItemMortar.Variants.WATER));
        OreDictionary.registerOre("portionWaterLarge", CuisineRegistry.ITEM_MORTAR.getItemStack(ItemMortar.Variants.WATER));
        OreDictionary.registerOre("foodFlour", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.FLOUR));
        OreDictionary.registerOre("foodDough", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.DOUGH));
        OreDictionary.registerOre("itemFoodCutter", CuisineRegistry.KITCHEN_KNIFE);
        OreDictionary.registerOre("logWood", new ItemStack(CuisineRegistry.LOG, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("treeSapling", new ItemStack(CuisineRegistry.SAPLING, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("foodDrink", CuisineRegistry.BOTTLE);

        // The wood handle
        OreDictionary.registerOre("handleWood", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.WOODEN_HANDLE));
        OreDictionary.registerOre("stickWood", CuisineRegistry.BAMBOO);

        CuisineRegistry.BASIC_FOOD.getVariants().forEach(ActionFactory.create(CuisineRegistry.BASIC_FOOD));
        CuisineRegistry.CROPS.getVariants().forEach(ActionFactory.create(CuisineRegistry.CROPS));
    }

    public static class ActionFactory
    {

        public static Consumer<Variant> create(ItemBasicFood item)
        {
            return v -> {
                ItemStack stack = item.getItemStack(v);
                Material material = CulinaryHub.API_INSTANCE.findMaterial(stack);
                if (material == null)
                {
                    return;
                }
                for (MaterialCategory category : MaterialCategory.values())
                {
                    if (category.getOreName() != null && material.isUnderCategoryOf(category))
                    {
                        OreDictionary.registerOre("listAll" + category.getOreName(), stack);
                        if (category == MaterialCategory.VEGETABLES)
                        {
                            OreDictionary.registerOre("ingredientKebabFill", stack);
                        }
                        if (category == MaterialCategory.GRAIN)
                        {
                            OreDictionary.registerOre("ingredientCereal", stack);
                        }
                    }
                }
            };
        }

    }
}
