package snownee.cuisine.plugins.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.api.process.prefab.DistillationBoiling;
import snownee.cuisine.tiles.TileBasinHeatable;

public class DistillationBoilingRecipe implements IRecipeWrapper
{
    final DistillationBoiling recipe;

    public DistillationBoilingRecipe(DistillationBoiling recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInput(VanillaTypes.FLUID, recipe.getInput());
        ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(Collections.emptyList(), getSources(recipe.getMinimumHeat())));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
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
