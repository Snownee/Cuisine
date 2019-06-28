package snownee.cuisine.crafting;

import java.util.Random;

import com.google.gson.JsonObject;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import snownee.cuisine.CuisineRegistry;
import snownee.kiwi.crafting.AbstractDynamicShapedRecipe;
import snownee.kiwi.util.OreUtil;

public class RecipeChoppingBoard extends AbstractDynamicShapedRecipe
{
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        for (int x = 0; x <= inv.getWidth() - getRecipeWidth(); ++x)
        {
            for (int y = 0; y <= inv.getHeight() - getRecipeHeight(); ++y)
            {
                if (checkMatch(inv, x, y))
                {
                    return CuisineRegistry.CHOPPING_BOARD.getItemStack(inv.getStackInRowAndColumn(x, y + 1));
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); i++)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.getItem().getToolClasses(stack).contains("axe"))
            {
                stack = stack.copy();
                stack.attemptDamageItem(1, new Random(), null);
                ret.set(i, stack);
            }
            else
            {
                ret.set(i, ForgeHooks.getContainerItem(stack));
            }
        }
        return ret;
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

    @Override
    protected boolean checkMatch(InventoryCrafting inv, int startX, int startY)
    {
        ItemStack axe = inv.getStackInRowAndColumn(startX, startY);
        if (!axe.getItem().getToolClasses(axe).contains("axe"))
        {
            return false;
        }
        ItemStack log = inv.getStackInRowAndColumn(startX, startY + 1);
        if (!OreUtil.doesItemHaveOreName(log, "logWood") || !(log.getItem() instanceof ItemBlock))
        {
            return false;
        }
        return checkEmpty(inv, startX, startY);
    }

    protected boolean checkEmpty(InventoryCrafting inv, int startX, int startY)
    {
        for (int x = 0; x < inv.getWidth(); ++x)
        {
            for (int y = 0; y < inv.getHeight(); ++y)
            {
                int subX = x - startX;
                int subY = y - startY;
                if (subX >= 0 && subY >= 0 && subX < this.getRecipeWidth() && subY < this.getRecipeHeight())
                {
                    continue;
                }

                ItemStack stack = inv.getStackInRowAndColumn(x, y);
                if (!Ingredient.EMPTY.apply(stack) && stack.getItem() != CuisineRegistry.KITCHEN_KNIFE)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public static final class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json)
        {
            return new RecipeChoppingBoard();
        }
    }
}
