package snownee.cuisine.plugins.jei;

import java.util.List;
import java.util.stream.Collectors;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import snownee.cuisine.api.process.Grinding;
import snownee.kiwi.crafting.input.ProcessingInput;

public class MortarGenericRecipe implements IRecipeWrapper
{
    private final Grinding recipe;

    MortarGenericRecipe(Grinding recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        List<List<ItemStack>> inputs = recipe.getInputs().stream().map(ProcessingInput::examples).collect(Collectors.toList());
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

}
