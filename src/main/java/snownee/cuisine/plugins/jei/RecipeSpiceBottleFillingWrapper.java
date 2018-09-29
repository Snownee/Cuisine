package snownee.cuisine.plugins.jei;

import java.util.List;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import snownee.cuisine.CuisineRegistry;
import snownee.kiwi.crafting.input.ProcessingInput;

public class RecipeSpiceBottleFillingWrapper implements IShapedCraftingRecipeWrapper
{
    private static final ItemStack bottle = new ItemStack(CuisineRegistry.SPICE_BOTTLE);
    private final ProcessingInput input;

    RecipeSpiceBottleFillingWrapper(ProcessingInput input)
    {
        this.input = input;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        List<ItemStack> inputs = input.examples();
        if (!inputs.isEmpty())
        {
            ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(inputs, ImmutableList.of(bottle)));
            ItemStack output = new ItemStack(CuisineRegistry.SPICE_BOTTLE);
            CuisineRegistry.SPICE_BOTTLE.getItemHandler(output).insertItem(0, inputs.get(0), false);
            ingredients.setOutput(VanillaTypes.ITEM, output);
        }
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
