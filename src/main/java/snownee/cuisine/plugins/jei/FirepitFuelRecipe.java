package snownee.cuisine.plugins.jei;

import java.awt.Color;
import java.util.Collections;

import com.google.common.base.Preconditions;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import snownee.cuisine.tiles.FuelHeatHandler.FuelInfo;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.crafting.input.ProcessingInput;

public class FirepitFuelRecipe implements IRecipeWrapper
{
    private final ProcessingInput input;
    private final IDrawableAnimated flame;
    private final String burnHeatString;
    private final String burnLevelString;

    public FirepitFuelRecipe(IGuiHelper guiHelper, ProcessingInput input, FuelInfo fuelInfo)
    {
        Preconditions.checkArgument(fuelInfo.heat > 0, "burn heat must be greater than 0");
        Preconditions.checkArgument(fuelInfo.level > 0, "burn level must be greater than 0");
        this.input = input;
        this.burnHeatString = I18nUtil.translate("gui.jei.brun_heat", fuelInfo.heat);
        this.burnLevelString = I18nUtil.translate("gui.jei.brun_level", fuelInfo.level);
        this.flame = guiHelper.drawableBuilder(JEICompat.VANILLA_RECIPE_GUI, 82, 114, 14, 14).buildAnimated(fuelInfo.level * 30, IDrawableAnimated.StartDirection.TOP, true);
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(input.examples()));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        flame.draw(minecraft, 1, 0);
        minecraft.fontRenderer.drawString(burnHeatString, 24, 7, Color.gray.getRGB());
        minecraft.fontRenderer.drawString(burnLevelString, 24, 20, Color.gray.getRGB());
    }
}
