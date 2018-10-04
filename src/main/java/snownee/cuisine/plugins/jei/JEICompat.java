package snownee.cuisine.plugins.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.process.Milling;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.process.Vessel;
import snownee.cuisine.blocks.BlockChoppingBoard;
import snownee.cuisine.internal.CuisineInternalGateway;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.items.ItemMortar;
import snownee.kiwi.util.definition.ItemDefinition;
import snownee.kiwi.util.definition.OreDictDefinition;

@JEIPlugin
public class JEICompat implements IModPlugin
{
    public static final List<ItemStack> AXES = Arrays.stream(CuisineConfig.PROGRESSION.axeList).map(id -> ItemDefinition.parse(id, false)).map(ItemDefinition::getItemStack).collect(Collectors.toList());

    @Override
    public void register(IModRegistry registry)
    {
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(CuisineRegistry.INGREDIENT));
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(CuisineRegistry.DRINK));
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.EMPOWERED_CITRON));

        registry.addRecipes(BlockChoppingBoard.getSuitableCovers().stream().map(RecipeChoppingBoardWrapper::new).collect(Collectors.toList()), VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipes(Collections.singletonList(new RecipeSpiceBottleEmptyWrapper()), VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipes(CuisineInternalGateway.INSTANCE.itemToSpiceMapping.keySet().stream().map(RecipeSpiceBottleFillingWrapper::new).collect(Collectors.toList()), VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipes(CuisineInternalGateway.INSTANCE.oreDictToSpiceMapping.keySet().stream().map(OreDictDefinition::of).map(RecipeSpiceBottleFillingWrapper::new).collect(Collectors.toList()), VanillaRecipeCategoryUid.CRAFTING);

        BlockChoppingBoard.getSuitableCovers().stream().map(CuisineRegistry.CHOPPING_BOARD::getItemStack).forEach(stack -> registry.addRecipeCatalyst(stack, ChoppingBoardRecipeCategory.UID));

        List<IRecipeWrapper> recipes = new ArrayList<>();
        CuisineInternalGateway.INSTANCE.itemToMaterialMapping.forEach((k, v) -> {
            if (!v.getValidForms().isEmpty())
            {
                recipes.add(new ChoppingBoardKnifeRecipe(k, v));
            }
        });
        CuisineInternalGateway.INSTANCE.oreDictToMaterialMapping.forEach((k, v) -> {
            if (!v.getValidForms().isEmpty())
            {
                recipes.add(new ChoppingBoardKnifeRecipe(OreDictDefinition.of(k), v));
            }
        });
        if (CuisineConfig.PROGRESSION.axeChopping)
        {
            Processing.CHOPPING.preview().forEach(recipe -> recipes.add(new ChoppingBoardAxeRecipe(recipe)));
        }
        registry.addRecipes(recipes, ChoppingBoardRecipeCategory.UID);

        recipes.clear();
        registry.addRecipeCatalyst(CuisineRegistry.ITEM_MORTAR.getItemStack(ItemMortar.Variants.EMPTY), MortarRecipeCategory.UID);
        Processing.GRINDING.preview().forEach(recipe -> recipes.add(new MortarGenericRecipe(recipe)));
        CuisineInternalGateway.INSTANCE.itemToMaterialMapping.forEach((k, v) -> {
            if (v.isValidForm(Form.PASTE) && Processing.GRINDING.findRecipe(k.getItemStack()) == null)
            {
                recipes.add(new MortarPasteRecipe(k, v));
            }
        });
        CuisineInternalGateway.INSTANCE.oreDictToMaterialMapping.forEach((k, v) -> {
            if (v.isValidForm(Form.PASTE) && Processing.GRINDING.findRecipe(OreDictDefinition.of(k).getItemStack()) == null)
            {
                recipes.add(new MortarPasteRecipe(OreDictDefinition.of(k), v));
            }
        });
        registry.addRecipes(recipes, MortarRecipeCategory.UID);

        registry.addRecipeCatalyst(new ItemStack(CuisineRegistry.MILL), MillRecipeCategory.UID);
        registry.handleRecipes(Milling.class, MillRecipe::new, MillRecipeCategory.UID);
        registry.addRecipes(Processing.MILLING.preview(), MillRecipeCategory.UID);

        registry.addRecipeCatalyst(new ItemStack(CuisineRegistry.JAR), VesselRecipeCategory.UID);
        registry.handleRecipes(Vessel.class, VesselRecipe::new, VesselRecipeCategory.UID);
        registry.addRecipes(Processing.VESSEL.preview(), VesselRecipeCategory.UID);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        registry.addRecipeCategories(new ChoppingBoardRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new MortarRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new MillRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new VesselRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }
}
