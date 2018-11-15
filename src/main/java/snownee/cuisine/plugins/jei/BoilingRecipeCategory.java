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
import mezz.jei.config.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.Cuisine;
import snownee.cuisine.util.I18nUtil;

public class BoilingRecipeCategory implements IRecipeCategory<GenericRecipeWrapper>
{
    static final String UID = Cuisine.MODID + ".basin_boiling";

    protected final IGuiHelper guiHelper;
    protected final IDrawable background;
    protected final IDrawable flame;
    protected final IDrawable container;
    protected final IDrawable flameOverlay;
    protected final String localizedName;

    public BoilingRecipeCategory(IGuiHelper guiHelper, IDrawable container)
    {
        this.guiHelper = guiHelper;
        this.container = container;
        background = guiHelper.createBlankDrawable(95, 32);
        flame = guiHelper.createDrawable(JEICompat.VANILLA_RECIPE_GUI, 2, 135, 13, 13);
        flameOverlay = guiHelper.createDrawable(Constants.RECIPE_GUI_VANILLA, 82, 114, 14, 14);
        localizedName = I18nUtil.translate("gui.jei.title.basin_boiling");
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
        flame.draw(minecraft, 32, 1);
        flameOverlay.draw(minecraft, 31, 0);
        container.draw(minecraft, 0, 22);
        container.draw(minecraft, 56, 22);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, GenericRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
        stacks.init(0, true, 1, 0);
        stacks.init(1, true, 29, 14);
        stacks.init(2, false, 77, 0);
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
        fluids.init(1, false, 58, 22, 16, 8, maxAmount, false, null);
        if (!ingredients.getInputs(VanillaTypes.ITEM).isEmpty() && !ingredients.getInputs(VanillaTypes.ITEM).get(0).isEmpty())
        {
            stacks.setBackground(0, guiHelper.getSlotDrawable());
        }
        stacks.setBackground(1, guiHelper.getSlotDrawable());
        if (!ingredients.getOutputs(VanillaTypes.ITEM).isEmpty() && !ingredients.getOutputs(VanillaTypes.ITEM).get(0).isEmpty())
        {
            stacks.setBackground(2, guiHelper.getSlotDrawable());
        }
        stacks.set(ingredients);
        fluids.set(ingredients);

        if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips)
        {
            stacks.addTooltipCallback(JEICompat.createRecipeIDTooltip(ItemStack.class, recipeWrapper.recipe));
            fluids.addTooltipCallback(JEICompat.createRecipeIDTooltip(FluidStack.class, recipeWrapper.recipe));
        }
    }

}
