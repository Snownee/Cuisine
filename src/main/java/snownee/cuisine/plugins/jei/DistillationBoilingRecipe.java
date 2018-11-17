package snownee.cuisine.plugins.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.api.process.prefab.DistillationBoiling;
import snownee.cuisine.tiles.TileBasinHeatable;

public class DistillationBoilingRecipe extends GenericRecipeWrapper<DistillationBoiling>
{
    public DistillationBoilingRecipe(DistillationBoiling recipe)
    {
        super(recipe);
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInput(VanillaTypes.FLUID, recipe.getInput());
        ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(Collections.emptyList(), getSources(recipe.getMinimumHeat())));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        JEICompat.arrowOut.draw(minecraft, 61, 11);
        JEICompat.arrowOutOverlay.draw(minecraft, 61, 11);
    }

    private static List<ItemStack> getSources(int minHeat)
    {
        List<ItemStack> sources = new ArrayList<>();
        if (minHeat == 0 && CuisineConfig.GENERAL.basinHeatingInDaylight)
        {
            sources.add(ItemStack.EMPTY);
        }
        TileBasinHeatable.STATE_HEAT_SOURCES.forEach((k, v) -> {
            if (v >= minHeat)
            {
                ItemStack stack = TileBasinHeatable.STATE_TO_ITEM.get(k);
                if (stack != null)
                {
                    sources.add(stack);
                }
            }
        });
        TileBasinHeatable.BLOCK_HEAT_SOURCES.forEach((k, v) -> {
            if (v >= minHeat)
            {
                ItemStack stack = TileBasinHeatable.BLOCK_TO_ITEM.get(k);
                if (stack != null)
                {
                    sources.add(stack);
                }
            }
        });
        return sources;
    }

}
