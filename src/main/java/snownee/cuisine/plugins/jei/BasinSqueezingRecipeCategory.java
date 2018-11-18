package snownee.cuisine.plugins.jei;

import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.Cuisine;
import snownee.cuisine.util.I18nUtil;

public class BasinSqueezingRecipeCategory implements IRecipeCategory<GenericRecipeWrapper>
{
    static final String UID = Cuisine.MODID + ".basin_squeezing";

    protected final IGuiHelper guiHelper;
    protected final IDrawable background;
    protected final IDrawable container;
    protected final IDrawable arrow;
    protected final IDrawable arrowOverlay;
    protected final String localizedName;

    public BasinSqueezingRecipeCategory(IGuiHelper guiHelper, IDrawable container)
    {
        this.guiHelper = guiHelper;
        this.container = container;
        background = guiHelper.createBlankDrawable(74, 32);
        arrow = guiHelper.createDrawable(JEICompat.VANILLA_RECIPE_GUI, 25, 133, 22, 15);
        arrowOverlay = guiHelper.drawableBuilder(JEICompat.VANILLA_RECIPE_GUI, 82, 128, 24, 17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        localizedName = I18nUtil.translate("gui.jei.title.basin_squeezing");
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
    public void drawExtras(Minecraft minecraft)
    {
        container.draw(minecraft, 0, 22);
        container.draw(minecraft, 54, 22);
        arrow.draw(minecraft, 26, 10);
        arrowOverlay.draw(minecraft, 26, 10);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, GenericRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
        stacks.init(0, true, 1, 0);
        if (!ingredients.getInputs(VanillaTypes.ITEM).isEmpty() && !ingredients.getInputs(VanillaTypes.ITEM).get(0).isEmpty())
        {
            stacks.setBackground(0, guiHelper.getSlotDrawable());
        }
        stacks.init(1, false, 55, 0);
        if (!ingredients.getInputs(VanillaTypes.ITEM).isEmpty() && !ingredients.getInputs(VanillaTypes.ITEM).get(0).isEmpty())
        {
            stacks.setBackground(0, guiHelper.getSlotDrawable());
        }
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
        fluids.init(0, true, 2, 22, 16, 8, maxAmount, false, null);
        fluids.init(1, false, 56, 22, 16, 8, maxAmount, false, null);
        stacks.set(ingredients);
        fluids.set(ingredients);

        if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips)
        {
            stacks.addTooltipCallback(JEICompat.createRecipeIDTooltip(ItemStack.class, recipeWrapper.recipe));
            fluids.addTooltipCallback(JEICompat.createRecipeIDTooltip(FluidStack.class, recipeWrapper.recipe));
        }
    }

}
