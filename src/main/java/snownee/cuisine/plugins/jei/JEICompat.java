package snownee.cuisine.plugins.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableBuilder;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.cuisine.api.process.Boiling;
import snownee.cuisine.api.process.CuisineProcessingRecipe;
import snownee.cuisine.api.process.Milling;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.process.Vessel;
import snownee.cuisine.api.process.prefab.DistillationBoiling;
import snownee.cuisine.api.process.prefab.MaterialSqueezing;
import snownee.cuisine.api.process.prefab.SimpleSqueezing;
import snownee.cuisine.api.process.prefab.SimpleThrowing;
import snownee.cuisine.blocks.BlockChoppingBoard;
import snownee.cuisine.internal.CuisineInternalGateway;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.items.ItemMortar;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.util.definition.ItemDefinition;
import snownee.kiwi.util.definition.OreDictDefinition;

@JEIPlugin
public class JEICompat implements IModPlugin
{
    // Keep an eye on this; this may change in the future
    static final ResourceLocation VANILLA_RECIPE_GUI = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");
    static final ResourceLocation CUISINE_RECIPE_GUI = new ResourceLocation(Cuisine.MODID, "textures/gui/jei.png");
    static final List<ItemStack> AXES = Arrays.stream(CuisineConfig.GENERAL.axeList).map(id -> ItemDefinition.parse(id, false)).map(ItemDefinition::getItemStack).collect(Collectors.toList());

    static IDrawable arrowOut;
    static IDrawable arrowOutOverlay;
    static IDrawable arrowIn;
    static IDrawable arrowInOverlay;
    static IDrawableStatic arrowInOverlayStatic;

    @Override
    public void register(IModRegistry registry)
    {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        arrowOut = guiHelper.createDrawable(JEICompat.CUISINE_RECIPE_GUI, 11, 26, 11, 7);
        arrowOutOverlay = guiHelper.drawableBuilder(JEICompat.CUISINE_RECIPE_GUI, 11, 18, 11, 8).buildAnimated(new CombinedTimer(80, 11, 11, 22), IDrawableAnimated.StartDirection.LEFT);
        arrowIn = guiHelper.createDrawable(JEICompat.CUISINE_RECIPE_GUI, 0, 26, 11, 7);
        IDrawableBuilder builder = guiHelper.drawableBuilder(JEICompat.CUISINE_RECIPE_GUI, 0, 18, 11, 8);
        arrowInOverlayStatic = builder.build();
        arrowInOverlay = builder.buildAnimated(new CombinedTimer(80, 0, 11, 22), IDrawableAnimated.StartDirection.LEFT);

        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(CuisineRegistry.INGREDIENT));
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(CuisineRegistry.DRINK));
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(CuisineRegistry.BOTTLE));
        registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(CuisineRegistry.BASIC_FOOD.getItemStack(ItemBasicFood.Variants.EMPOWERED_CITRON));

        registry.addRecipes(BlockChoppingBoard.getSuitableCovers().stream().map(RecipeChoppingBoardWrapper::new).collect(Collectors.toList()), VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipes(CuisineInternalGateway.INSTANCE.itemToSpiceMapping.keySet().stream().map(RecipeSpiceBottleFillingWrapper::new).collect(Collectors.toList()), VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipes(CuisineInternalGateway.INSTANCE.oreDictToSpiceMapping.keySet().stream().map(OreDictDefinition::of).map(RecipeSpiceBottleFillingWrapper::new).collect(Collectors.toList()), VanillaRecipeCategoryUid.CRAFTING);

        BlockChoppingBoard.getSuitableCovers().stream().map(CuisineRegistry.CHOPPING_BOARD::getItemStack).forEach(stack -> registry.addRecipeCatalyst(stack, ChoppingBoardRecipeCategory.UID));

        // TODO (3TUSK): Now I have to clean up the mess that I made long time ago - IngredientDefinition - ItemDefinition, but for Ingredient.
        // TODO (3TUSK): As you can see, there is no way to distinguish Form. A separate map for form-sensitive mapping?
        // Why IdentityHashMap? Material vs. Ingredient is akin to Item vs. ItemStack (i.e. flyweight pattern), so
        // Material instances are shared, and thus are safe to be compared by `==`.
        // And yes, as you can see, this reverse map excludes juice. So it's not a general purpose reverse map which is
        // exactly why it is a local variable right now - until we found a way to generalize it, it is local variable.
        IdentityHashMap<Material, List<ItemStack>> reverseMaterialMapWithoutJuice = new IdentityHashMap<>();
        for (Map.Entry<ItemDefinition, Ingredient> entry : CuisineInternalGateway.INSTANCE.itemIngredients.entrySet())
        {
            if (entry.getValue().getForm() != Form.JUICE)
            {
                reverseMaterialMapWithoutJuice.computeIfAbsent(entry.getValue().getMaterial(), m -> new ArrayList<>()).addAll(entry.getKey().examples());
            }
        }
        for (Map.Entry<String, Ingredient> entry : CuisineInternalGateway.INSTANCE.oreDictIngredients.entrySet())
        {
            if (entry.getValue().getForm() != Form.JUICE)
            {
                reverseMaterialMapWithoutJuice.computeIfAbsent(entry.getValue().getMaterial(), m -> new ArrayList<>()).addAll(OreDictionary.getOres(entry.getKey()));
            }
        }

        List<IRecipeWrapper> recipes = new ArrayList<>();
        CuisineInternalGateway.INSTANCE.itemIngredients.forEach((k, v) -> {
            if (v.getForm() == Form.FULL && !v.getMaterial().getValidForms().isEmpty())
            {
                if (!v.getMaterial().getValidForms().equals(Form.JUICE_ONLY))
                {
                    recipes.add(new ChoppingBoardKnifeRecipe(k, v.getMaterial()));
                }
            }
        });
        CuisineInternalGateway.INSTANCE.oreDictIngredients.forEach((k, v) -> {
            if (v.getForm() == Form.FULL && !v.getMaterial().getValidForms().isEmpty())
            {
                if (!v.getMaterial().getValidForms().equals(Form.JUICE_ONLY) && !OreDictionary.getOres(k, false).isEmpty())
                {
                    recipes.add(new ChoppingBoardKnifeRecipe(OreDictDefinition.of(k), v.getMaterial()));
                }
            }
        });
        if (CuisineConfig.GENERAL.axeChopping)
        {
            Processing.CHOPPING.preview().forEach(recipe -> recipes.add(new ChoppingBoardAxeRecipe(recipe)));
        }
        registry.addRecipes(recipes, ChoppingBoardRecipeCategory.UID);

        recipes.clear();
        registry.addRecipeCatalyst(CuisineRegistry.ITEM_MORTAR.getItemStack(ItemMortar.Variants.EMPTY), MortarRecipeCategory.UID);
        Processing.GRINDING.preview().forEach(recipe -> recipes.add(new MortarGenericRecipe(recipe)));
        CuisineInternalGateway.INSTANCE.itemIngredients.forEach((k, v) -> {
            if (v.getForm() != Form.PASTE && v.getForm() != Form.JUICE && v.getMaterial().isValidForm(Form.PASTE) && Processing.GRINDING.findRecipe(k.getItemStack()) == null)
            {
                recipes.add(new MortarPasteRecipe(k, v.getMaterial()));
            }
        });
        CuisineInternalGateway.INSTANCE.oreDictIngredients.forEach((k, v) -> {
            if (v.getForm() != Form.PASTE && v.getForm() != Form.JUICE && v.getMaterial().isValidForm(Form.PASTE) && !OreDictionary.getOres(k, false).isEmpty() && Processing.GRINDING.findRecipe(OreDictDefinition.of(k).getItemStack()) == null)
            {
                recipes.add(new MortarPasteRecipe(OreDictDefinition.of(k), v.getMaterial()));
            }
        });
        registry.addRecipes(recipes, MortarRecipeCategory.UID);
        recipes.clear();

        registry.addRecipeCatalyst(new ItemStack(CuisineRegistry.MILL), MillRecipeCategory.UID);
        registry.handleRecipes(Milling.class, MillRecipe::new, MillRecipeCategory.UID);
        registry.addRecipes(Processing.MILLING.preview(), MillRecipeCategory.UID);

        registry.addRecipeCatalyst(new ItemStack(CuisineRegistry.JAR), VesselRecipeCategory.UID);
        registry.handleRecipes(Vessel.class, VesselRecipe::new, VesselRecipeCategory.UID);
        registry.addRecipes(Processing.VESSEL.preview(), VesselRecipeCategory.UID);

        registry.addRecipeCatalyst(new ItemStack(CuisineRegistry.WOODEN_BASIN), BasinSqueezingRecipeCategory.UID, BasinThrowingRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(CuisineRegistry.EARTHEN_BASIN), BasinSqueezingRecipeCategory.UID, BasinThrowingRecipeCategory.UID, BoilingRecipeCategory.UID);
        for (int i = 0; i < CuisineRegistry.EARTHEN_BASIN_COLORED.getItemSubtypeAmount(); i++)
        {
            registry.addRecipeCatalyst(new ItemStack(CuisineRegistry.EARTHEN_BASIN_COLORED, 1, i), BasinSqueezingRecipeCategory.UID, BasinThrowingRecipeCategory.UID, BoilingRecipeCategory.UID);
        }
        // registry.handleRecipes(BasinInteracting.class, BasinSqueezingRecipe::new, BasinSqueezingRecipeCategory.UID);
        // registry.handleRecipes(BasinInteracting.class, BasinThrowingRecipe::new, BasinThrowingRecipeCategory.UID);
        // registry.handleRecipes(Boiling.class, BoilingRecipe::new, BoilingRecipeCategory.UID);
        for (BasinInteracting recipe : Processing.SQUEEZING.preview())
        {
            if (recipe instanceof SimpleSqueezing)
            {
                recipes.add(new SimpleSqueezingRecipe((SimpleSqueezing) recipe));
            }
            else if (recipe instanceof MaterialSqueezing)
            {
                if (((MaterialSqueezing) recipe).getMaterial() != CulinaryHub.CommonMaterials.EMPOWERED_CITRON)
                {
                    recipes.add(new MaterialSqueezingRecipe((MaterialSqueezing) recipe, reverseMaterialMapWithoutJuice.get(((MaterialSqueezing) recipe).getMaterial())));
                }
            }
        }
        registry.addRecipes(recipes, BasinSqueezingRecipeCategory.UID);
        recipes.clear();
        for (BasinInteracting recipe : Processing.BASIN_THROWING.preview())
        {
            if (recipe instanceof SimpleThrowing)
            {
                recipes.add(new SimpleThrowingRecipe((SimpleThrowing) recipe));
            }
        }
        registry.addRecipes(recipes, BasinThrowingRecipeCategory.UID);
        recipes.clear();
        for (Boiling recipe : Processing.BOILING.preview())
        {
            if (recipe instanceof DistillationBoiling)
            {
                recipes.add(new DistillationBoilingRecipe((DistillationBoiling) recipe));
            }
        }
        registry.addRecipes(recipes, BoilingRecipeCategory.UID);
        recipes.clear();
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        IDrawable basin = guiHelper.createDrawable(JEICompat.CUISINE_RECIPE_GUI, 0, 33, 20, 10);
        registry.addRecipeCategories(new ChoppingBoardRecipeCategory(guiHelper));
        registry.addRecipeCategories(new MortarRecipeCategory(guiHelper));
        registry.addRecipeCategories(new MillRecipeCategory(guiHelper));
        registry.addRecipeCategories(new VesselRecipeCategory(guiHelper));
        registry.addRecipeCategories(new BoilingRecipeCategory(guiHelper, basin));
        registry.addRecipeCategories(new BasinSqueezingRecipeCategory(guiHelper, basin));
        registry.addRecipeCategories(new BasinThrowingRecipeCategory(guiHelper, basin));
    }

    /**
     * @deprecated use {@link #identifierTooltip(ResourceLocation)} is sufficient.
     * @param clazz type token
     * @param recipe the recipe object
     * @param <T> the type of ingredient
     * @return a tooltip callback object that provides recipe identifier
     */
    @Deprecated
    static <T> ITooltipCallback<T> createRecipeIDTooltip(Class<T> clazz, CuisineProcessingRecipe recipe)
    {
        return identifierTooltip(recipe.getIdentifier());
    }

    private static <T> ITooltipCallback<T> identifierTooltip(ResourceLocation locator)
    {
        return (slot, isInput, ingredient, tooltip) ->
        {
            if (!isInput)
            {
                tooltip.add(TextFormatting.DARK_GRAY + I18nUtil.translate("jei.tooltip.recipe.id", locator));
            }
        };
    }
}
