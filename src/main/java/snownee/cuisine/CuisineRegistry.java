package snownee.cuisine;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.common.EnumPlantType;
import snownee.cuisine.blocks.BlockBamboo;
import snownee.cuisine.blocks.BlockBambooPlant;
import snownee.cuisine.blocks.BlockBasin;
import snownee.cuisine.blocks.BlockBasinColored;
import snownee.cuisine.blocks.BlockChoppingBoard;
import snownee.cuisine.blocks.BlockCorn;
import snownee.cuisine.blocks.BlockCuisineCrops;
import snownee.cuisine.blocks.BlockDoubleCrops;
import snownee.cuisine.blocks.BlockDrinkro;
import snownee.cuisine.blocks.BlockFirePit;
import snownee.cuisine.blocks.BlockJar;
import snownee.cuisine.blocks.BlockMill;
import snownee.cuisine.blocks.BlockModDoor;
import snownee.cuisine.blocks.BlockModLeaves;
import snownee.cuisine.blocks.BlockModLog;
import snownee.cuisine.blocks.BlockModSapling;
import snownee.cuisine.blocks.BlockModTrapdoor;
import snownee.cuisine.blocks.BlockMortar;
import snownee.cuisine.blocks.BlockPlacedDish;
import snownee.cuisine.blocks.BlockShearedLeaves;
import snownee.cuisine.blocks.BlockSqueezer;
import snownee.cuisine.blocks.BlockTofu;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.items.ItemBottle;
import snownee.cuisine.items.ItemCrops;
import snownee.cuisine.items.ItemDish;
import snownee.cuisine.items.ItemDrink;
import snownee.cuisine.items.ItemDrinkro;
import snownee.cuisine.items.ItemEmptyPlate;
import snownee.cuisine.items.ItemFan;
import snownee.cuisine.items.ItemIngredient;
import snownee.cuisine.items.ItemIronSpatula;
import snownee.cuisine.items.ItemKitchenKnife;
import snownee.cuisine.items.ItemManual;
import snownee.cuisine.items.ItemModBoat;
import snownee.cuisine.items.ItemModDoor;
import snownee.cuisine.items.ItemMortar;
import snownee.cuisine.items.ItemSpiceBottle;
import snownee.cuisine.potions.PotionDispersal;
import snownee.cuisine.potions.PotionDrunk;
import snownee.cuisine.potions.PotionSustainedRelease;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.block.BlockMod;
import snownee.kiwi.block.BlockModFence;
import snownee.kiwi.block.BlockModFenceGate;
import snownee.kiwi.item.ItemMod;
import snownee.kiwi.item.ItemModVariantsNew;
import snownee.kiwi.potion.PotionMod;
import snownee.kiwi.util.definition.ItemDefinition;

@KiwiModule(modid = Cuisine.MODID)
public class CuisineRegistry implements IModule
{
    public static final BlockTofu TOFU_BLOCK = new BlockTofu("tofu_block");

    public static final BlockBamboo BAMBOO = new BlockBamboo("bamboo");

    public static final BlockBambooPlant BAMBOO_PLANT = new BlockBambooPlant("bamboo_plant");

    public static final ItemCrops CROPS = new ItemCrops("crops");

    public static final BlockCuisineCrops PEANUT = new BlockCuisineCrops("peanut", ItemDefinition.of(CROPS, ItemCrops.Variant.PEANUT.getMeta()));
    public static final BlockCuisineCrops SESAME = new BlockCuisineCrops("sesame", ItemDefinition.of(CROPS, ItemCrops.Variant.SESAME.getMeta()));
    public static final BlockCuisineCrops SOYBEAN = new BlockCuisineCrops("soybean", ItemDefinition.of(CROPS, ItemCrops.Variant.SOYBEAN.getMeta()));
    public static final BlockCuisineCrops RICE = new BlockCuisineCrops("rice", EnumPlantType.Water, ItemDefinition.of(CROPS, ItemCrops.Variant.RICE.getMeta()));
    public static final BlockCuisineCrops TOMATO = new BlockCuisineCrops("tomato", ItemDefinition.of(CROPS, ItemCrops.Variant.TOMATO.getMeta()));
    public static final BlockCuisineCrops CHILI = new BlockCuisineCrops("chili", EnumPlantType.Nether, ItemDefinition.of(CROPS, ItemCrops.Variant.CHILI.getMeta()));
    public static final BlockCuisineCrops GARLIC = new BlockCuisineCrops("garlic", ItemDefinition.of(CROPS, ItemCrops.Variant.GARLIC.getMeta()));
    public static final BlockCuisineCrops GINGER = new BlockCuisineCrops("ginger", ItemDefinition.of(CROPS, ItemCrops.Variant.GINGER.getMeta()));
    public static final BlockCuisineCrops SICHUAN_PEPPER = new BlockCuisineCrops("sichuan_pepper", ItemDefinition.of(CROPS, ItemCrops.Variant.SICHUAN_PEPPER.getMeta()));
    public static final BlockCuisineCrops SCALLION = new BlockCuisineCrops("scallion", ItemDefinition.of(CROPS, ItemCrops.Variant.SCALLION.getMeta()));
    public static final BlockCuisineCrops TURNIP = new BlockCuisineCrops("turnip", ItemDefinition.of(CROPS, ItemCrops.Variant.TURNIP.getMeta()));
    public static final BlockCuisineCrops CHINESE_CABBAGE = new BlockCuisineCrops("chinese_cabbage", ItemDefinition.of(CROPS, ItemCrops.Variant.CHINESE_CABBAGE.getMeta()));
    public static final BlockCuisineCrops LETTUCE = new BlockCuisineCrops("lettuce", ItemDefinition.of(CROPS, ItemCrops.Variant.LETTUCE.getMeta()));
    public static final BlockCuisineCrops CORN = new BlockCorn("corn");
    public static final BlockDoubleCrops CUCUMBER = new BlockDoubleCrops("cucumber", ItemDefinition.of(CROPS, ItemCrops.Variant.CUCUMBER.getMeta()));
    public static final BlockCuisineCrops GREEN_PEPPER = new BlockCuisineCrops("green_pepper", ItemDefinition.of(CROPS, ItemCrops.Variant.GREEN_PEPPER.getMeta()));
    public static final BlockCuisineCrops RED_PEPPER = new BlockCuisineCrops("red_pepper", ItemDefinition.of(CROPS, ItemCrops.Variant.RED_PEPPER.getMeta()));
    public static final BlockCuisineCrops LEEK = new BlockCuisineCrops("leek", ItemDefinition.of(CROPS, ItemCrops.Variant.LEEK.getMeta()));
    public static final BlockCuisineCrops ONION = new BlockCuisineCrops("onion", ItemDefinition.of(CROPS, ItemCrops.Variant.ONION.getMeta()));
    public static final BlockCuisineCrops EGGPLANT = new BlockCuisineCrops("eggplant", ItemDefinition.of(CROPS, ItemCrops.Variant.EGGPLANT.getMeta()));
    public static final BlockCuisineCrops SPINACH = new BlockCuisineCrops("spinach", ItemDefinition.of(CROPS, ItemCrops.Variant.SPINACH.getMeta()));

    public static final BlockMortar MORTAR = new BlockMortar("mortar");

    public static final BlockJar JAR = new BlockJar("jar");

    public static final BlockMill MILL = new BlockMill("mill");

    public static final BlockPlacedDish PLACED_DISH = new BlockPlacedDish("placed_dish");
    public static final ItemEmptyPlate EMPTY_PLATE = new ItemEmptyPlate(PLACED_DISH);

    public static final BlockFirePit FIRE_PIT = new BlockFirePit("fire_pit");
    
//    public static final BlockUtensil FRYING_PAN = new BlockUtensil("frying_pan", TileFryingPan::new, new AxisAlignedBB(0d, 0d, 0d, 1d, 0.225d, 1d));

    public static final BlockChoppingBoard CHOPPING_BOARD = new BlockChoppingBoard("chopping_board");

    public static final BlockBasin WOODEN_BASIN = new BlockBasin("wooden_basin", Material.WOOD);

    public static final BlockBasin EARTHEN_BASIN = new BlockBasin("earthen_basin", Material.ROCK);

    public static final BlockBasinColored EARTHEN_BASIN_COLORED = new BlockBasinColored("earthen_basin_colored", Material.ROCK);

    public static final BlockSqueezer SQUEEZER = new BlockSqueezer("squeezer");

    public static final BlockDrinkro DRINKRO = new BlockDrinkro("drinkro");
    public static final ItemDrinkro ITEM_DRINKRO = new ItemDrinkro(DRINKRO);

    public static final ItemBasicFood<Void, ItemBasicFood.Variant> BASIC_FOOD = new ItemBasicFood<Void, ItemBasicFood.Variant>("food", ItemBasicFood.Variant.values());

    public static final BlockModLog LOG = new BlockModLog("log");
    public static final BlockMod PLANKS = new BlockMod("planks", Material.WOOD);
    public static final BlockModSapling SAPLING = new BlockModSapling("sapling");
    public static final BlockModLeaves LEAVES_CITRON = new BlockModLeaves("leaves_citron", ItemBasicFood.Variant.CITRON);
    public static final BlockModLeaves LEAVES_GRAPEFRUIT = new BlockModLeaves("leaves_grapefruit", ItemBasicFood.Variant.GRAPEFRUIT);
    public static final BlockModLeaves LEAVES_LEMON = new BlockModLeaves("leaves_lemon", ItemBasicFood.Variant.LEMON);
    public static final BlockModLeaves LEAVES_LIME = new BlockModLeaves("leaves_lime", ItemBasicFood.Variant.LIME);
    public static final BlockModLeaves LEAVES_MANDARIN = new BlockModLeaves("leaves_mandarin", ItemBasicFood.Variant.MANDARIN);
    public static final BlockModLeaves LEAVES_ORANGE = new BlockModLeaves("leaves_orange", ItemBasicFood.Variant.ORANGE);
    public static final BlockModLeaves LEAVES_POMELO = new BlockModLeaves("leaves_pomelo", ItemBasicFood.Variant.POMELO);

    public static final BlockShearedLeaves SHEARED_LEAVES = new BlockShearedLeaves("sheared_leaves");

    public static final BlockModTrapdoor TRAPDOOR = new BlockModTrapdoor("trapdoor", Material.WOOD);

    public static final BlockModDoor DOOR = new BlockModDoor("door", Material.WOOD);
    public static final ItemModDoor ITEM_DOOR = new ItemModDoor(DOOR);

    public static final BlockModFence FENCE = new BlockModFence("fence", PLANKS.getDefaultState());
    public static final BlockModFenceGate FENCE_GATE = new BlockModFenceGate("fence_gate", PLANKS.getDefaultState());

    public static final ItemIronSpatula IRON_SPATULA = new ItemIronSpatula("iron_spatula");

    public static final ItemFan FAN = new ItemFan("fan");

    public static final ItemDish DISH = new ItemDish("dish");

    public static final ItemDrink DRINK = new ItemDrink("drink");

    public static final ItemBottle BOTTLE = new ItemBottle("glass_bottle");

    public static final ItemMortar ITEM_MORTAR = new ItemMortar(MORTAR.getName(), MORTAR);

    public static final ItemIngredient INGREDIENT = new ItemIngredient();

    public static final ItemKitchenKnife KITCHEN_KNIFE = new ItemKitchenKnife("kitchen_knife");

    public static final ItemSpiceBottle SPICE_BOTTLE = new ItemSpiceBottle("spice_bottle");

    public static final ItemMod WOK = new ItemMod("wok");

//    public static final ItemMod FRYING_PAN = new ItemMod("frying_pan");

    public static final ItemManual MANUAL = new ItemManual("manual");

    public static final ItemModBoat BOAT = new ItemModBoat("boat");

    public static final ItemModVariantsNew MATERIAL = new ItemModVariantsNew("material", Cuisine.Materials.values());

    public static final PotionDispersal DISPERSAL = new PotionDispersal("dispersal", 0);

    public static final PotionMod HOT = new PotionMod("hot", true, 1, false, 0xff4500, -1, true);

    public static final PotionMod EFFECT_RESISTANCE = new PotionMod("effect_resistance", true, 2, true, 0xccccff, -1, false, false);

    public static final PotionMod COLD_BLOOD = new PotionMod("cold_blood", true, 3, false, 0x9F79EE, -1, true);

    public static final PotionMod TOUGHNESS = new PotionMod("toughness", true, 4, false, 0x943943, -1, true);

    public static final PotionSustainedRelease SUSTAINED_RELEASE = new PotionSustainedRelease("sustained_release");

    public static final PotionDrunk DRUNK = new PotionDrunk("drunk", 5);

    @Override
    public void init()
    {
        Item.getItemFromBlock(PLACED_DISH).setContainerItem(Item.getItemFromBlock(PLACED_DISH));
        WOK.setCreativeTab(Cuisine.CREATIVE_TAB).setContainerItem(WOK);
        PLANKS.setCreativeTab(Cuisine.CREATIVE_TAB);
        TRAPDOOR.setCreativeTab(Cuisine.CREATIVE_TAB).setHardness(3.0F);
        FENCE.setCreativeTab(Cuisine.CREATIVE_TAB);
        FENCE_GATE.setCreativeTab(Cuisine.CREATIVE_TAB);
        MATERIAL.setCreativeTab(Cuisine.CREATIVE_TAB);
    }
}
