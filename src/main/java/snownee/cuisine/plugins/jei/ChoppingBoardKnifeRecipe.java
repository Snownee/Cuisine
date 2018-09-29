package snownee.cuisine.plugins.jei;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.Material;
import snownee.kiwi.crafting.input.ProcessingInput;

public class ChoppingBoardKnifeRecipe implements IRecipeWrapper
{
    private static final ItemStack KNIFE = new ItemStack(CuisineRegistry.KITCHEN_KNIFE);

    private final ProcessingInput input;
    private final Material output;

    ChoppingBoardKnifeRecipe(ProcessingInput input, Material material)
    {
        this.input = input;
        this.output = material;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        List<ItemStack> examples = input.examples();
        if (examples.isEmpty())
        {
            return;
        }
        ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(examples, Collections.singletonList(KNIFE)));
        ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(JEICompat.getAllPossibleFormsExceptFullAndJuice(output)));
    }

    // TODO: tooltip: form help

}
