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

public class MortarRecipeCategory implements IRecipeCategory
{
    static final String UID = Cuisine.MODID + ".mortar";

    private final IDrawable background;
    private final String localizedName;

    public MortarRecipeCategory(IGuiHelper guiHelper)
    {
        background = guiHelper.createDrawable(JEICompat.CUISINE_RECIPE_GUI, 0, 0, 148, 18);
        localizedName = I18n.format(CuisineRegistry.MORTAR.getTranslationKey() + ".name");
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
        for (int i = 0; i < 5; i++)
        {
            items.init(i, true, i * 18, 0);
        }
        items.init(5, false, 130, 0);
        items.set(ingredients);

        if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips && recipeWrapper instanceof GenericRecipeWrapper)
        {
            items.addTooltipCallback(JEICompat.createRecipeIDTooltip(ItemStack.class, ((GenericRecipeWrapper) recipeWrapper).recipe));
        }
    }

}
