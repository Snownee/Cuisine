package snownee.cuisine.plugins.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import snownee.cuisine.Cuisine;
import snownee.cuisine.util.I18nUtil;

public class FirepitFuelCategory implements IRecipeCategory<FirepitFuelRecipe>
{
    static final String UID = Cuisine.MODID + ".firepit_fuel";

    private final IDrawable background;
    private final String localizedName;

    public FirepitFuelCategory(IGuiHelper guiHelper)
    {
        background = guiHelper.drawableBuilder(JEICompat.VANILLA_RECIPE_GUI, 0, 134, 18, 34).addPadding(0, 0, 0, 88).build();
        localizedName = I18nUtil.translate("gui.jei.title.firepit_fuel");
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
    public void setRecipe(IRecipeLayout recipeLayout, FirepitFuelRecipe recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 0, 16);
        guiItemStacks.set(ingredients);
    }
}
