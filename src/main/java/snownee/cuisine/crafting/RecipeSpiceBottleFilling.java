package snownee.cuisine.crafting;

import com.google.gson.JsonObject;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryHub;
import snownee.kiwi.crafting.AbstractDynamicShapedRecipe;

public class RecipeSpiceBottleFilling extends AbstractDynamicShapedRecipe
{
    @Override
    protected boolean checkMatch(InventoryCrafting inv, int startX, int startY)
    {
        ItemStack spice = inv.getStackInRowAndColumn(startX, startY);
        if (CulinaryHub.API_INSTANCE.findSpice(spice) == null)
        {
            return false;
        }
        ItemStack bottle = inv.getStackInRowAndColumn(startX, startY + 1);
        if (bottle.getItem() != CuisineRegistry.SPICE_BOTTLE || !CuisineRegistry.SPICE_BOTTLE.isContainerEmpty(bottle))
        {
            return false;
        }
        return checkEmpty(inv, startX, startY);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack bottle = ItemStack.EMPTY;
        ItemStack spice = ItemStack.EMPTY;
        for (int k = 0; k < inv.getSizeInventory(); ++k)
        {
            ItemStack stack = inv.getStackInSlot(k);
            if (!stack.isEmpty())
            {
                if (stack.getItem() == CuisineRegistry.SPICE_BOTTLE)
                {
                    bottle = stack.copy();
                    bottle.setCount(1);
                }
                else
                {
                    spice = stack;
                }
            }
        }
        if (bottle.isEmpty() || spice.isEmpty())
        {
            return ItemStack.EMPTY;
        }
        CuisineRegistry.SPICE_BOTTLE.getItemHandler(bottle).insertItem(0, spice, false);
        return bottle;
    }

    @Override
    public int getRecipeWidth()
    {
        return 1;
    }

    @Override
    public int getRecipeHeight()
    {
        return 2;
    }

    public static final class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json)
        {
            return new RecipeSpiceBottleFilling();
        }
    }
}
