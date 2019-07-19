package snownee.cuisine.crafting;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.FoodContainer;
import snownee.kiwi.crafting.AbstractDynamicShapedRecipe;

public class RecipeFoodEmpty extends AbstractDynamicShapedRecipe
{

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        for (int k = 0; k < inv.getSizeInventory(); ++k)
        {
            ItemStack stack = inv.getStackInSlot(k);
            if (!stack.isEmpty() && stack.hasTagCompound())
            {
                // TODO (3TUSK): Rigorously determine empty container type
                return new ItemStack(stack.getItem(), stack.getCount(), stack.getMetadata());
            }
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
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

    @Override
    protected boolean checkMatch(InventoryCrafting inv, int startX, int startY)
    {
        ItemStack stack = inv.getStackInRowAndColumn(startX, startY);
        if (stack.hasTagCompound() && checkEmpty(inv, startX, startY))
        {
            // TODO (3TUSK): Rigorously determine empty container type
            return true;
        }
        return false;
    }

    public static final class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json)
        {
            return new RecipeFoodEmpty();
        }
    }

}
