package snownee.cuisine.plugins.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import snownee.cuisine.Cuisine;

public class BasinSqueezingRecipeCategory implements IRecipeCategory
{
    static final String UID = Cuisine.MODID + ".basin_squeezing";
    private static final ResourceLocation VANILLA_RECIPE_GUI = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");

    private final IDrawable background;
    private final String localizedName;

    public BasinSqueezingRecipeCategory(IGuiHelper guiHelper)
    {
        background = guiHelper.drawableBuilder(VANILLA_RECIPE_GUI, 49, 168, 76, 18).addPadding(18, 15, 0, 0).build();
        localizedName = I18n.format("gui.jei.title.basin_squeezing");
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
        // TODO Auto-generated method stub

    }

}
