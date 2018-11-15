package snownee.cuisine.plugins.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;

public class MillRecipeCategory implements IRecipeCategory<MillRecipe>
{
    public static final String UID = Cuisine.MODID + '.' + "mill";

    private final IDrawable background;
    private final String localizedName;

    MillRecipeCategory(IGuiHelper helper)
    {
        background = helper.createDrawable(new ResourceLocation(Cuisine.MODID, "textures/gui/jei.png"), 54, 0, 112, 18);
        localizedName = I18n.format(CuisineRegistry.MILL.getTranslationKey() + ".name");
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
    public void setRecipe(IRecipeLayout layout, MillRecipe recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup stacks = layout.getItemStacks();
        IGuiFluidStackGroup fluids = layout.getFluidStacks();
        stacks.init(0, true, 0, 0);
        if (recipe.recipe.getInputFluid() != null)
        {
            fluids.init(0, true, 19, 1, 16, 16, recipe.recipe.getInputFluid().amount, false, null);
        }
        if (recipe.recipe.getOutput().isEmpty()) // If there is no item output...
        {
            if (recipe.recipe.getOutputFluid() != null) // ... and there is a fluid output, put fluid output to the left
            {
                fluids.init(1, false, 77, 1, 16, 16, recipe.recipe.getOutputFluid().amount, false, null);
            }
        }
        else
        {
            stacks.init(1, false, 76, 0); // Otherwise item output goes first.
            if (recipe.recipe.getOutputFluid() != null)
            {
                fluids.init(1, false, 95, 1, 16, 16, recipe.recipe.getOutputFluid().amount, false, null);
            }
        }
        stacks.set(ingredients);
        fluids.set(ingredients);

        if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips)
        {
            stacks.addTooltipCallback(JEICompat.createRecipeIDTooltip(ItemStack.class, recipe.recipe));
            fluids.addTooltipCallback(JEICompat.createRecipeIDTooltip(FluidStack.class, recipe.recipe));
        }
    }
}
