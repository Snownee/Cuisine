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
import snownee.kiwi.item.IVariant;

public class OreDictHandler
{
    public static void init()
    {
        // NUT
        OreDictionary.registerOre("cropPeanut", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.PEANUT));

        // GRAIN
        OreDictionary.registerOre("cropWheat", Items.WHEAT);
        OreDictionary.registerOre("listAllgrain", Items.WHEAT);
        OreDictionary.registerOre("cropSesame", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.SESAME));
        OreDictionary.registerOre("cropRice", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.RICE));
        OreDictionary.registerOre("cropSoybean", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.SOYBEAN));
        OreDictionary.registerOre("cropCorn", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.CORN));
        OreDictionary.registerOre("foodRice", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.WHITE_RICE));

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
        OreDictionary.registerOre("cropTomato", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.TOMATO));
        OreDictionary.registerOre("cropChilipepper", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.CHILI));
        OreDictionary.registerOre("cropGarlic", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.GARLIC));
        OreDictionary.registerOre("cropGinger", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.GINGER));
        OreDictionary.registerOre("cropScallion", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.SCALLION));
        OreDictionary.registerOre("cropTurnip", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.TURNIP));
        OreDictionary.registerOre("cropCabbage", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.CHINESE_CABBAGE));
        OreDictionary.registerOre("cropLettuce", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.LETTUCE));
        OreDictionary.registerOre("cropCucumber", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.CUCUMBER));
        OreDictionary.registerOre("cropBellpepper", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.RED_PEPPER));
        OreDictionary.registerOre("cropBellpepper", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.GREEN_PEPPER));
        OreDictionary.registerOre("cropLeek", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.LEEK));
        OreDictionary.registerOre("cropOnion", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.ONION));
        OreDictionary.registerOre("cropEggplant", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.EGGPLANT));
        OreDictionary.registerOre("cropSpinach", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.SPINACH));
        OreDictionary.registerOre("cropBambooshoot", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.BAMBOO_SHOOT));

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
        OreDictionary.registerOre("cropSichuanpepper", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.SICHUAN_PEPPER));
        OreDictionary.registerOre("listAllspice", CuisineRegistry.CROPS.getItemStack(ItemCrops.Variant.SICHUAN_PEPPER));

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
        OreDictionary.registerOre("cropMandarin", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.MANDARIN));
        OreDictionary.registerOre("cropCitron", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.CITRON));
        OreDictionary.registerOre("cropPomelo", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.POMELO));
        OreDictionary.registerOre("cropOrange", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.ORANGE));
        OreDictionary.registerOre("cropLemon", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.LEMON));
        OreDictionary.registerOre("cropLime", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.LIME));
        OreDictionary.registerOre("cropGrapefruit", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.GRAPEFRUIT));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.MANDARIN));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.CITRON));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.POMELO));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.ORANGE));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.LEMON));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.LIME));
        OreDictionary.registerOre("listAllcitrus", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.GRAPEFRUIT));

        // MISC
        OreDictionary.registerOre("foodFirmtofu", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.TOFU));
        OreDictionary.registerOre("listAllwater", CuisineRegistry.ITEM_MORTAR.getItemStack(ItemMortar.Variant.WATER));
        OreDictionary.registerOre("listAllwater", Items.WATER_BUCKET);
        OreDictionary.registerOre("portionWaterLarge", CuisineRegistry.ITEM_MORTAR.getItemStack(ItemMortar.Variant.WATER));
        OreDictionary.registerOre("foodFlour", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.FLOUR));
        OreDictionary.registerOre("foodDough", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.DOUGH));
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
        OreDictionary.registerOre("foodPickles", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.PICKLED_CABBAGE));
        OreDictionary.registerOre("foodPickles", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.PICKLED_CUCUMBER));
        OreDictionary.registerOre("foodPickles", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.PICKLED_PEPPER));
        OreDictionary.registerOre("foodPickles", CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variant.PICKLED_TURNIP));

        // The wood handle
        OreDictionary.registerOre("handleWood", CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.WOODEN_HANDLE));

        CuisineRegistry.BASIC_FOOD.getVariants().forEach(ActionFactory.create(CuisineRegistry.BASIC_FOOD));
        CuisineRegistry.CROPS.getVariants().forEach(ActionFactory.create(CuisineRegistry.CROPS));
    }

    public static class ActionFactory
    {

        public static Consumer<IVariant> create(ItemBasicFood item)
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
