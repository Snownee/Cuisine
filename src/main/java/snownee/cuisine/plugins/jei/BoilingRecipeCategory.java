package snownee.cuisine.plugins.jei;

import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.Cuisine;

public class BoilingRecipeCategory implements IRecipeCategory
{
    static final String UID = Cuisine.MODID + ".basin_boiling";
    private static final ResourceLocation VANILLA_RECIPE_GUI = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");

    private final IDrawable background;
    private final String localizedName;

    public BoilingRecipeCategory(IGuiHelper guiHelper)
    {
        background = guiHelper.drawableBuilder(VANILLA_RECIPE_GUI, 49, 168, 76, 18).addPadding(18, 15, 0, 0).build();
        localizedName = I18n.format("gui.jei.title.basin_boiling");
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
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
        stacks.init(0, true, 0, 0);
        stacks.init(1, true, 16, 0);
        stacks.init(2, false, 32, 0);
        int maxAmount = 0;
        List<List<FluidStack>> lists = Lists.newArrayList(ingredients.getInputs(VanillaTypes.FLUID));
        lists.addAll(ingredients.getOutputs(VanillaTypes.FLUID));
        for (List<FluidStack> fluidStacks : lists)
        {
            for (FluidStack fluidStack : fluidStacks)
            {
                if (fluidStack != null && fluidStack.amount > maxAmount)
                {
                    maxAmount = fluidStack.amount;
                }
            }
        }
        fluids.init(0, true, 0, 36, 16, 8, 200, false, null);
        fluids.init(1, false, 36, 36);
        stacks.set(ingredients);
        fluids.set(ingredients);
    }

}
