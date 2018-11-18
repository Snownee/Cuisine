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
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.Cuisine;
import snownee.cuisine.util.I18nUtil;

public class BasinThrowingRecipeCategory implements IRecipeCategory<GenericRecipeWrapper>
{
    static final String UID = Cuisine.MODID + ".basin_throwing";

    protected final IGuiHelper guiHelper;
    protected final IDrawable background;
    protected final IDrawable container;
    protected final String localizedName;

    public BasinThrowingRecipeCategory(IGuiHelper guiHelper, IDrawable container)
    {
        this.guiHelper = guiHelper;
        this.container = container;
        background = guiHelper.createBlankDrawable(94, 32);
        localizedName = I18nUtil.translate("gui.jei.title.basin_throwing");
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
    public IDrawable getIcon()
    {
        return JEICompat.arrowInOverlayStatic;
    }

    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        container.draw(minecraft, 19, 22);
        container.draw(minecraft, 55, 22);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, GenericRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
        stacks.init(0, true, 0, 0);
        if (!ingredients.getInputs(VanillaTypes.ITEM).isEmpty() && !ingredients.getInputs(VanillaTypes.ITEM).get(0).isEmpty())
        {
            stacks.setBackground(0, guiHelper.getSlotDrawable());
        }
        stacks.init(1, false, 76, 0);
        if (!ingredients.getOutputs(VanillaTypes.ITEM).isEmpty() && !ingredients.getOutputs(VanillaTypes.ITEM).get(0).isEmpty())
        {
            stacks.setBackground(1, guiHelper.getSlotDrawable());
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
        fluids.init(0, true, 21, 22, 16, 8, maxAmount, false, null);
        fluids.init(1, false, 57, 22, 16, 8, maxAmount, false, null);
        stacks.set(ingredients);
        fluids.set(ingredients);

        if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips)
        {
            stacks.addTooltipCallback(JEICompat.createRecipeIDTooltip(ItemStack.class, recipeWrapper.recipe));
            fluids.addTooltipCallback(JEICompat.createRecipeIDTooltip(FluidStack.class, recipeWrapper.recipe));
        }
    }

}
