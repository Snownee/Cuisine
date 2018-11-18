package snownee.cuisine.plugins.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.util.I18nUtil;

public class VesselRecipeCategory implements IRecipeCategory<VesselRecipe>
{
    static final String UID = Cuisine.MODID + ".vessel";

    private static final ITooltipCallback<FluidStack> SOLVENT_TIP = (
            slotIndex, input, ingredient, tooltip
    ) -> tooltip.add(I18nUtil.translate("tip.solvent"));

    private final IDrawable background;
    private final String localizedName;

    VesselRecipeCategory(IGuiHelper guiHelper)
    {
        background = guiHelper.createDrawable(new ResourceLocation(Cuisine.MODID, "textures/gui/jei.png"), 36, 0, 130, 18);
        localizedName = I18n.format(CuisineRegistry.JAR.getTranslationKey() + ".name");
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
    public void setRecipe(IRecipeLayout recipeLayout, VesselRecipe recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();

        stacks.init(0, true, 0, 0);
        stacks.init(1, true, 36, 0);
        stacks.init(2, false, 94, 0);

        fluids.init(0, true, 19, 1, 16, 16, 100, false, null);
        if (recipeWrapper.recipe.getOutputFluid() != null)
        {
            fluids.init(1, false, recipeWrapper.recipe.getOutput().isEmpty() ? 95 : 113, 1, 16, 16, recipeWrapper.recipe.getOutputFluid().amount, false, null);
        }
        else
        {
            fluids.addTooltipCallback(SOLVENT_TIP);
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
