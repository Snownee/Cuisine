package snownee.cuisine.events;

import java.util.function.Consumer;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Ingredient;
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
        OreDictionary.registerOre("cropRice", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.RICE));
        OreDictionary.registerOre("cropSoybean", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.SOYBEAN));
        OreDictionary.registerOre("cropCorn", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.CORN));
        OreDictionary.registerOre("foodRice", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.WHITE_RICE));

        // VEGGIE
        OreDictionary.registerOre("cropBeetroot", Items.BEETROOT);
        OreDictionary.registerOre("listAllveggie", Items.BEETROOT);
        OreDictionary.registerOre("cropPotato", Items.POTATO);
        OreDictionary.registerOre("listAllveggie", Items.POTATO);
        OreDictionary.registerOre("cropCarrot", Items.CARROT);
        OreDictionary.registerOre("listAllveggie", Items.CARROT);
        OreDictionary.registerOre("cropPumpkin", Blocks.PUMPKIN);
        OreDictionary.registerOre("listAllveggie", Blocks.PUMPKIN);
        OreDictionary.registerOre("foodMushroom", Blocks.BROWN_MUSHROOM);
        OreDictionary.registerOre("foodMushroom", Blocks.RED_MUSHROOM);
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
        OreDictionary.registerOre("cropBambooshoot", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variants.BAMBOO_SHOOT));

        // SPICE
        OreDictionary.registerOre("dustSalt", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.SALT));
        OreDictionary.registerOre("listAllspice", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.SALT));
        OreDictionary.registerOre("dustCrudesalt", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.CRUDE_SALT));
        OreDictionary.registerOre("listAllspice", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.CRUDE_SALT));
        OreDictionary.registerOre("dustUnrefinedsugar", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.UNREFINED_SUGAR));
        OreDictionary.registerOre("listAllspice", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.UNREFINED_SUGAR));
        OreDictionary.registerOre("listAllsugar", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.UNREFINED_SUGAR));
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
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.MANDARIN));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.CITRON));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.POMELO));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.ORANGE));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.LEMON));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.LIME));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.GRAPEFRUIT));

        // MISC
        OreDictionary.registerOre("foodFirmtofu", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.TOFU));
        OreDictionary.registerOre("listAllwater", CuisineRegistry.ITEM_MORTAR.getItemStack(ItemMortar.Variants.WATER));
        OreDictionary.registerOre("listAllwater", Items.WATER_BUCKET);
        OreDictionary.registerOre("portionWaterLarge", CuisineRegistry.ITEM_MORTAR.getItemStack(ItemMortar.Variants.WATER));
        OreDictionary.registerOre("foodFlour", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.FLOUR));
        OreDictionary.registerOre("foodDough", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.DOUGH));
        OreDictionary.registerOre("itemFoodCutter", CuisineRegistry.KITCHEN_KNIFE);
        OreDictionary.registerOre("logWood", CuisineRegistry.LOG);
        OreDictionary.registerOre("plankWood", CuisineRegistry.PLANKS);
        OreDictionary.registerOre("stickWood", CuisineRegistry.BAMBOO);
        OreDictionary.registerOre("doorWood", CuisineRegistry.ITEM_DOOR);
        OreDictionary.registerOre("fenceWood", CuisineRegistry.FENCE);
        OreDictionary.registerOre("fenceGateWood", CuisineRegistry.FENCE_GATE);
        OreDictionary.registerOre("treeSapling", new ItemStack(CuisineRegistry.SAPLING, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("treeLeaves", new ItemStack(CuisineRegistry.SHEARED_LEAVES, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("foodDrink", CuisineRegistry.BOTTLE);
        OreDictionary.registerOre("toolMortarandpestle", CuisineRegistry.ITEM_MORTAR);
        OreDictionary.registerOre("toolSkillet", CuisineRegistry.WOK);
        OreDictionary.registerOre("toolBakeware", CuisineRegistry.PLACED_DISH);

        // foodPickles
        OreDictionary.registerOre("foodPickles", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.PICKLED_CABBAGE));
        OreDictionary.registerOre("foodPickles", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.PICKLED_CUCUMBER));
        OreDictionary.registerOre("foodPickles", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.PICKLED_PEPPER));
        OreDictionary.registerOre("foodPickles", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.PICKLED_TURNIP));

        // The wood handle
        OreDictionary.registerOre("handleWood", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.WOODEN_HANDLE));

        CuisineRegistry.BASIC_FOOD.getVariants().forEach(ActionFactory.create(CuisineRegistry.BASIC_FOOD));
        CuisineRegistry.CROPS.getVariants().forEach(ActionFactory.create(CuisineRegistry.CROPS));
    }

    public static class ActionFactory
    {

        public static Consumer<Variant> create(ItemBasicFood item)
        {
            return v -> {
                boolean loaded = Loader.isModLoaded("vanillafoodpantry");
                ItemStack stack = item.getItemStack(v);
                Ingredient ingredient = CulinaryHub.API_INSTANCE.findIngredient(stack);
                if (ingredient != null)
                {
                    for (MaterialCategory category : MaterialCategory.values())
                    {
                        if (category.getOreName() != null && ingredient.getMaterial().isUnderCategoryOf(category))
                        {
                            OreDictionary.registerOre("listAll" + category.getOreName(), stack);
                            if (loaded)
                            {
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
                    }
                }
            };
        }

    }
}
