package snownee.cuisine.library;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.util.ItemNBTUtil;

public class RarityManager
{
    private static final String RARITY_TAG = Cuisine.MODID + ":Rarity";

    public static ItemStack setRarity(ItemStack stack, EnumRarity rarity)
    {
        return setRarity(stack, rarity.ordinal());
    }

    public static ItemStack setRarity(ItemStack stack, int rarity)
    {
        if (!stack.isEmpty())
        {
            if (rarity == 0)
            {
                ItemNBTUtil.removeTag(stack, RARITY_TAG);
            }
            else
            {
                ItemNBTUtil.setInt(stack, RARITY_TAG, rarity);
            }
        }
        return stack;
    }

    public static EnumRarity getRarity(ItemStack stack)
    {
        int rarity = stack.getRarity().ordinal() + ItemNBTUtil.getInt(stack, RARITY_TAG, 0);
        EnumRarity[] values = EnumRarity.values();
        rarity = MathHelper.clamp(rarity, 0, values.length - 1);
        return values[rarity];
    }
}
