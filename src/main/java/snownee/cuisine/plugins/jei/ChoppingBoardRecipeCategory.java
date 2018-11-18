package snownee.cuisine.plugins.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;

public class ChoppingBoardRecipeCategory implements IRecipeCategory
{
    static final String UID = Cuisine.MODID + ".chopping_board";

    private final IDrawable background;
    private final String localizedName;

    ChoppingBoardRecipeCategory(IGuiHelper guiHelper)
    {
        background = guiHelper.drawableBuilder(JEICompat.VANILLA_RECIPE_GUI, 49, 168, 76, 18).addPadding(18, 15, 0, 0).build();
        localizedName = I18n.format(CuisineRegistry.CHOPPING_BOARD.getTranslationKey() + ".name");
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

        if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips && recipeWrapper instanceof GenericRecipeWrapper)
        {
            items.addTooltipCallback(JEICompat.createRecipeIDTooltip(ItemStack.class, ((GenericRecipeWrapper) recipeWrapper).recipe));
        }
    }
}
