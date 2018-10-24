package snownee.cuisine.plugins.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.process.prefab.MaterialSqueezing;
import snownee.cuisine.fluids.FluidJuice;
import snownee.cuisine.items.ItemIngredient;

public class MaterialSqueezingRecipe implements IRecipeWrapper
{
    private final Material squeezeInput;
    private Collection<ItemStack> extraInputs;

    MaterialSqueezingRecipe(MaterialSqueezing recipe, Collection<ItemStack> extraInputs)
    {
        this.squeezeInput = recipe.getMaterial();
        this.extraInputs = extraInputs;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        List<ItemStack> inputs1 = ItemIngredient.getAllValidFormsWithException(squeezeInput, EnumSet.of(Form.FULL, Form.PASTE, Form.JUICE));
        List<ItemStack> inputs2 = new ArrayList<>(extraInputs);
        inputs2.addAll(inputs1);
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(inputs2));
        ingredients.setOutput(VanillaTypes.FLUID, FluidJuice.make(squeezeInput, 250));
    }

}
