package snownee.cuisine.plugins.jei;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import snownee.cuisine.CuisineRegistry;

public class RecipeChoppingBoardWrapper implements IShapedCraftingRecipeWrapper
{
    private static final boolean showKnife = Loader.isModLoaded("cfm");
    private static final List<ItemStack> Knife = Collections.singletonList(showKnife ? new ItemStack(CuisineRegistry.KITCHEN_KNIFE) : ItemStack.EMPTY);
    private final ItemStack cover;

    RecipeChoppingBoardWrapper(ItemStack cover)
    {
        this.cover = cover;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        List<List<ItemStack>> inputs;
        if (showKnife)
        {
            inputs = Arrays.asList(JEICompat.AXES, Knife, Collections.singletonList(cover));
        }
        else
        {
            inputs = Arrays.asList(JEICompat.AXES, Collections.singletonList(cover));
        }
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, CuisineRegistry.CHOPPING_BOARD.getItemStack(cover));
    }

    @Override
    public int getWidth()
    {
        return showKnife ? 2 : 1;
    }

    @Override
    public int getHeight()
    {
        return 2;
    }

}
