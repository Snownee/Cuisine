package snownee.cuisine.crafting;

import com.google.gson.JsonObject;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import snownee.cuisine.CuisineRegistry;
import snownee.kiwi.crafting.AbstractDynamicShapedRecipe;

public class RecipeSpiceBottleEmpty extends AbstractDynamicShapedRecipe
{
    @Override
    protected boolean checkMatch(InventoryCrafting inv, int startX, int startY)
    {
        ItemStack stack = inv.getStackInRowAndColumn(startX, startY);
        return stack.getItem() == CuisineRegistry.SPICE_BOTTLE && !CuisineRegistry.SPICE_BOTTLE.isContainerEmpty(stack) && checkEmpty(inv, startX, startY);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        return new ItemStack(CuisineRegistry.SPICE_BOTTLE);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    @Override
    public int getRecipeWidth()
    {
        return 1;
    }

    @Override
    public int getRecipeHeight()
    {
        return 1;
    }

    public static final class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json)
        {
            return new RecipeSpiceBottleEmpty();
        }
    }
}
