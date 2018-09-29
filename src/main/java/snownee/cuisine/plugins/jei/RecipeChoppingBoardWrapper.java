package snownee.cuisine.plugins.jei;

import java.util.Arrays;
import java.util.Collections;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import snownee.cuisine.CuisineRegistry;

public class RecipeChoppingBoardWrapper implements IShapedCraftingRecipeWrapper
{
    private final ItemStack cover;

    RecipeChoppingBoardWrapper(ItemStack cover)
    {
        this.cover = cover;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(JEICompat.AXES, Collections.singletonList(cover)));
        ingredients.setOutput(VanillaTypes.ITEM, CuisineRegistry.CHOPPING_BOARD.getItemStack(cover));
    }

    @Override
    public int getWidth()
    {
        return 1;
    }

    @Override
    public int getHeight()
    {
        return 2;
    }

}
