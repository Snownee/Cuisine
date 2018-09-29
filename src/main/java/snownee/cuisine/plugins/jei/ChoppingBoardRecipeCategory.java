package snownee.cuisine.plugins.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;

@SuppressWarnings("deprecation")
public class ChoppingBoardRecipeCategory implements IRecipeCategory
{
    static final String UID = Cuisine.MODID + ".chopping_board";

    // Keep an eye on this; this may change in the future
    private static final ResourceLocation VANILLA_RECIPE_GUI = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");

    private final IDrawable background;
    private final String localizedName;

    public ChoppingBoardRecipeCategory(IGuiHelper guiHelper)
    {
        // I am not sure NetEase will support this high version JEI
        background = guiHelper.createDrawable(VANILLA_RECIPE_GUI, 49, 168, 76, 18, 18, 15, 0, 0);
        localizedName = I18n.format(CuisineRegistry.ITEM_CHOPPING_BOARD.getTranslationKey() + ".name");
    }

    @Override
    public String getUid()
    {
        return UID;
    }

    @Override
    public String getTitle()
    {
        return localizedName;
    }

    @Override
    public String getModName()
    {
        return Cuisine.NAME;
    }

    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup items = recipeLayout.getItemStacks();
        items.init(0, true, 0, 18);
        items.init(1, true, 30, 5);
        items.init(2, false, 58, 18);
        items.set(ingredients);
        //items.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
        //    tooltip.add("TEST TOOLTIP PLEASE IGNORE");
        //}); // To Snownee: next time you should just kill this lambda entirely
    }
}
