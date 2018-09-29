package snownee.cuisine.plugins.jei;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import snownee.cuisine.CuisineRegistry;

public class RecipeSpiceBottleEmptyWrapper implements IShapedCraftingRecipeWrapper
{
    RecipeSpiceBottleEmptyWrapper()
    {
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        NonNullList<ItemStack> inputs = NonNullList.create();
        CuisineRegistry.SPICE_BOTTLE.getSubItems(CuisineRegistry.SPICE_BOTTLE.getCreativeTab(), inputs);
        inputs.removeIf(CuisineRegistry.SPICE_BOTTLE::isContainerEmpty);
        ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(inputs));
        ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(CuisineRegistry.SPICE_BOTTLE));
    }

    @Override
    public int getWidth()
    {
        return 1;
    }

    @Override
    public int getHeight()
    {
        return 1;
    }
}
