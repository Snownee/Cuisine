package snownee.cuisine.crafting;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.DyeUtils;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.util.ItemNBTUtil;

public class DrinkBrewingRecipe implements IBrewingRecipe
{

    @Override
    public boolean isInput(ItemStack input)
    {
        return input.getItem() == CuisineRegistry.BOTTLE;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient)
    {
        return PotionHelper.isTypeConversionReagent(ingredient) || DyeUtils.isDye(ingredient);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        ItemStack dummy = makeDummyPotionItem(input);
        if (PotionHelper.hasTypeConversions(dummy, ingredient))
        {
            ItemStack dummyOutput = PotionHelper.doReaction(ingredient, dummy);
            if (!dummyOutput.isEmpty() && dummyOutput.hasTagCompound() && dummyOutput.getTagCompound().hasKey("Potion", Constants.NBT.TAG_STRING))
            {
                ItemStack output = input.copy();
                ItemNBTUtil.setString(output, "potion", ItemNBTUtil.getString(dummyOutput, "Potion", "empty"));
                return output;
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack makeDummyPotionItem(ItemStack stack)
    {
        if (stack.getItem() == CuisineRegistry.BOTTLE)
        {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("potion", Constants.NBT.TAG_STRING))
            {
                ItemStack dummy = new ItemStack(Items.POTIONITEM);
                ItemNBTUtil.setString(dummy, "Potion", ItemNBTUtil.getString(stack, "potion", "empty"));
                return dummy;
            }
            else
            {
                return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.AWKWARD);
            }
        }
        return ItemStack.EMPTY;
    }
}
