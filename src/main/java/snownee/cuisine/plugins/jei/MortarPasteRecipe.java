package snownee.cuisine.plugins.jei;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Material;
import snownee.cuisine.items.ItemIngredient;
import snownee.kiwi.crafting.input.ProcessingInput;

public class MortarPasteRecipe implements IRecipeWrapper
{
    private final ProcessingInput input;
    private final Material material;

    public MortarPasteRecipe(ProcessingInput input, Material v)
    {
        this.input = input;
        this.material = v;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        List<ItemStack> examples = input.examples();
        examples = examples.stream().filter(i -> CulinaryHub.API_INSTANCE.findMaterial(i) == material).collect(Collectors.toList());
        if (examples.isEmpty())
        {
            return;
        }
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(examples));
        ingredients.setOutput(VanillaTypes.ITEM, ItemIngredient.make(material, Form.PASTE));
    }

}
