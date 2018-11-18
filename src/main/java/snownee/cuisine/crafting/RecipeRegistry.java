package snownee.cuisine.crafting;

import com.google.common.collect.ImmutableList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.process.Chopping;
import snownee.cuisine.api.process.Grinding;
import snownee.cuisine.api.process.Milling;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.process.Vessel;
import snownee.cuisine.api.process.prefab.DistillationBoiling;
import snownee.cuisine.api.process.prefab.MaterialSqueezing;
import snownee.cuisine.api.process.prefab.SimpleSqueezing;
import snownee.cuisine.api.process.prefab.SimpleThrowing;
import snownee.cuisine.fluids.CuisineFluids;
import snownee.cuisine.fluids.FluidJuice;
import snownee.cuisine.items.ItemBasicFood;
import snownee.kiwi.util.definition.ItemDefinition;
import snownee.kiwi.util.definition.OreDictDefinition;

public class RecipeRegistry
{
    public static void preInit()
    {
    }

    public static void init()
    {
        if (CuisineConfig.GENERAL.axeChopping)
        {
            Processing.CHOPPING.add(new Chopping(OreDictDefinition.of("plankWood"), new ItemStack(Items.STICK, 4)));
            Processing.CHOPPING.add(new Chopping(ItemDefinition.of(Blocks.LOG), new ItemStack(Blocks.PLANKS, 6)));
            Processing.CHOPPING.add(new Chopping(ItemDefinition.of(Blocks.LOG, 1), new ItemStack(Blocks.PLANKS, 6, 1)));
            Processing.CHOPPING.add(new Chopping(ItemDefinition.of(Blocks.LOG, 2), new ItemStack(Blocks.PLANKS, 6, 2)));
            Processing.CHOPPING.add(new Chopping(ItemDefinition.of(Blocks.LOG, 3), new ItemStack(Blocks.PLANKS, 6, 3)));
            Processing.CHOPPING.add(new Chopping(ItemDefinition.of(Blocks.LOG2), new ItemStack(Blocks.PLANKS, 6, 4)));
            Processing.CHOPPING.add(new Chopping(ItemDefinition.of(Blocks.LOG2, 1), new ItemStack(Blocks.PLANKS, 6, 5)));
            Processing.CHOPPING.add(new Chopping(ItemDefinition.of(CuisineRegistry.LOG), new ItemStack(CuisineRegistry.PLANKS, 6)));
        }

        Processing.GRINDING.add(new Grinding(ImmutableList.of(OreDictDefinition.of("cropRice", 1)), CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.WHITE_RICE), 4));
        Processing.GRINDING.add(new Grinding(ImmutableList.of(OreDictDefinition.of("dustCrudesalt", 1)), CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.SALT), 10));

        Processing.MILLING.add(new Milling(OreDictDefinition.of("cropChilipepper"), CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.CHILI_POWDER)));
        Processing.MILLING.add(new Milling(OreDictDefinition.of("cropSichuanpepper"), CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.SICHUAN_PEPPER_POWDER)));
        Processing.MILLING.add(new Milling(new ItemStack(Items.WHEAT), CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.FLOUR)));
        Processing.MILLING.add(new Milling(OreDictDefinition.of("foodRice"), CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.RICE_POWDER)));
        Processing.MILLING.add(new Milling(OreDictDefinition.of("dustCrudesalt"), CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.SALT)));
        Processing.MILLING.add(new Milling(OreDictDefinition.of("cropSesame"), ItemStack.EMPTY, null, new FluidStack(CuisineFluids.SESAME_OIL, 20)));
        Processing.MILLING.add(new Milling(OreDictDefinition.of("cropPeanut"), ItemStack.EMPTY, null, new FluidStack(CuisineFluids.EDIBLE_OIL, 100)));
        Processing.MILLING.add(new Milling(OreDictDefinition.of("cropRice"), ItemStack.EMPTY, null, new FluidStack(CuisineFluids.EDIBLE_OIL, 20)));
        Processing.MILLING.add(new Milling(OreDictDefinition.of("seedRice"), ItemStack.EMPTY, null, new FluidStack(CuisineFluids.EDIBLE_OIL, 20)));
        Processing.MILLING.add(new Milling(OreDictDefinition.of("cropCorn"), ItemStack.EMPTY, null, new FluidStack(CuisineFluids.EDIBLE_OIL, 100)));
        Processing.MILLING.add(new Milling(new ItemStack(Items.BEETROOT_SEEDS), ItemStack.EMPTY, null, new FluidStack(CuisineFluids.EDIBLE_OIL, 20)));
        Processing.MILLING.add(new Milling(new ItemStack(Items.MELON_SEEDS), ItemStack.EMPTY, null, new FluidStack(CuisineFluids.EDIBLE_OIL, 40)));
        Processing.MILLING.add(new Milling(new ItemStack(Items.PUMPKIN_SEEDS), ItemStack.EMPTY, null, new FluidStack(CuisineFluids.EDIBLE_OIL, 40)));
        Processing.MILLING.add(new Milling(new ItemStack(Items.WHEAT_SEEDS), ItemStack.EMPTY, null, new FluidStack(CuisineFluids.EDIBLE_OIL, 20)));
        Processing.MILLING.add(new Milling(OreDictDefinition.of("cropSoybean"), ItemStack.EMPTY, new FluidStack(FluidRegistry.WATER, 100), new FluidStack(CuisineFluids.SOY_MILK, 100)));

        Processing.VESSEL.add(new Vessel(ItemDefinition.of(CuisineRegistry.BASIC_FOOD, ItemBasicFood.Variants.RICE_POWDER.getMeta()), FluidRegistry.WATER, ItemDefinition.EMPTY, new FluidStack(CuisineFluids.RICE_VINEGAR, 20)));
        Processing.VESSEL.add(new Vessel(OreDictDefinition.of("cropRice"), FluidRegistry.WATER, ItemDefinition.EMPTY, new FluidStack(CuisineFluids.RICE_VINEGAR, 30)));
        Processing.VESSEL.add(new Vessel(OreDictDefinition.of("foodRice"), FluidRegistry.WATER, ItemDefinition.EMPTY, new FluidStack(CuisineFluids.RICE_VINEGAR, 20)));
        Processing.VESSEL.add(new Vessel(OreDictDefinition.of("cropChilipepper"), FluidRegistry.WATER, ItemDefinition.of(CuisineRegistry.BASIC_FOOD, ItemBasicFood.Variants.PICKLED_PEPPER.getMeta()), null, OreDictDefinition.of("dustSalt")));
        Processing.VESSEL.add(new Vessel(OreDictDefinition.of("cropCucumber"), FluidRegistry.WATER, ItemDefinition.of(CuisineRegistry.BASIC_FOOD, ItemBasicFood.Variants.PICKLED_CUCUMBER.getMeta()), null, OreDictDefinition.of("dustSalt")));
        Processing.VESSEL.add(new Vessel(OreDictDefinition.of("cropCabbage"), FluidRegistry.WATER, ItemDefinition.of(CuisineRegistry.BASIC_FOOD, ItemBasicFood.Variants.PICKLED_CABBAGE.getMeta()), null, OreDictDefinition.of("dustSalt")));
        Processing.VESSEL.add(new Vessel(OreDictDefinition.of("cropTurnip"), FluidRegistry.WATER, ItemDefinition.of(CuisineRegistry.BASIC_FOOD, ItemBasicFood.Variants.PICKLED_TURNIP.getMeta()), null, OreDictDefinition.of("dustSalt")));
        Processing.VESSEL.add(new Vessel(OreDictDefinition.of("cropSoybean"), FluidRegistry.WATER, ItemDefinition.EMPTY, new FluidStack(CuisineFluids.SOY_SAUCE, 20)));
        Processing.VESSEL.add(new Vessel(ItemDefinition.of(Items.APPLE), FluidRegistry.WATER, ItemDefinition.EMPTY, new FluidStack(CuisineFluids.FRUIT_VINEGAR, 10)));
        Processing.VESSEL.add(new Vessel(ItemDefinition.of(Items.GOLDEN_APPLE), FluidRegistry.WATER, ItemDefinition.EMPTY, new FluidStack(CuisineFluids.FRUIT_VINEGAR, 20)));
        Processing.VESSEL.add(new Vessel(ItemDefinition.of(Items.GOLDEN_APPLE, 1), FluidRegistry.WATER, ItemDefinition.EMPTY, new FluidStack(CuisineFluids.FRUIT_VINEGAR, 100)));
        Processing.VESSEL.add(new Vessel(ItemDefinition.of(Items.MELON), FluidRegistry.WATER, ItemDefinition.EMPTY, new FluidStack(CuisineFluids.FRUIT_VINEGAR, 5)));
        Processing.VESSEL.add(new Vessel(ItemDefinition.of(Blocks.MELON_BLOCK), FluidRegistry.WATER, ItemDefinition.EMPTY, new FluidStack(CuisineFluids.FRUIT_VINEGAR, 50)));

        Processing.BOILING.add(new DistillationBoiling(new FluidStack(FluidRegistry.WATER, 200), CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.CRUDE_SALT), 0));
        Processing.BOILING.add(new DistillationBoiling(new FluidStack(CuisineFluids.SUGARCANE_JUICE, 200), CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.UNREFINED_SUGAR), 2));
        Processing.BOILING.add(new DistillationBoiling(FluidJuice.make(CulinaryHub.CommonMaterials.BEETROOT, 200), CuisineRegistry.MATERIAL.getItemStack(Cuisine.Materials.UNREFINED_SUGAR), 2));

        ItemStack sugar = new ItemStack(Items.SUGAR);
        Processing.BASIN_THROWING.add(new SimpleThrowing(new ResourceLocation(Cuisine.MODID, "sugar_from_bamboo_and_sugarcane"), ItemDefinition.of(CuisineRegistry.MATERIAL, Cuisine.Materials.BAMBOO_CHARCOAL.getMeta()), new FluidStack(CuisineFluids.SUGARCANE_JUICE, 200), sugar));
        Processing.BASIN_THROWING.add(new SimpleThrowing(new ResourceLocation(Cuisine.MODID, "sugar_from_charcoal_and_sugarcane"), ItemDefinition.of(Items.COAL, 1), new FluidStack(CuisineFluids.SUGARCANE_JUICE, 200), sugar));
        Processing.BASIN_THROWING.add(new SimpleThrowing(new ResourceLocation(Cuisine.MODID, "sugar_from_bamboo_and_beet"), ItemDefinition.of(CuisineRegistry.MATERIAL, Cuisine.Materials.BAMBOO_CHARCOAL.getMeta()), FluidJuice.make(CulinaryHub.CommonMaterials.BEETROOT, 200), sugar));
        Processing.BASIN_THROWING.add(new SimpleThrowing(new ResourceLocation(Cuisine.MODID, "sugar_from_charcoal_and_beet"), ItemDefinition.of(Items.COAL, 1), FluidJuice.make(CulinaryHub.CommonMaterials.BEETROOT, 200), sugar));

        Processing.SQUEEZING.add(new SimpleSqueezing(new ResourceLocation(Cuisine.MODID, "sugarcane_squeezing"), OreDictDefinition.of("sugarcane"), new FluidStack(CuisineFluids.SUGARCANE_JUICE, 200)));

        CulinaryHub.API_INSTANCE.getKnownMaterials().stream().filter(m -> m.isValidForm(Form.JUICE)).filter(m -> m.isUnderCategoryOf(MaterialCategory.FRUIT) || m.isUnderCategoryOf(MaterialCategory.VEGETABLES)).forEach(m -> Processing.SQUEEZING.add(new MaterialSqueezing(m)));

    }
}
