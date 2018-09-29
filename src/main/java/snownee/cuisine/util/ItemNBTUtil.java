package snownee.cuisine.util;

import java.awt.Color;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import snownee.cuisine.Cuisine;

public class ItemNBTUtil
{

    public static boolean hasNBT(ItemStack stack)
    {
        return stack.hasTagCompound();
    }

    public static void initNBT(ItemStack stack)
    {
        if (!hasNBT(stack))
        {
            stack.setTagCompound(new NBTTagCompound());
        }
    }

    public static void injectNBT(ItemStack stack, NBTTagCompound nbt)
    {
        stack.setTagCompound(nbt);
    }

    public static NBTTagCompound getNBT(ItemStack stack)
    {
        initNBT(stack);
        return stack.getTagCompound();
    }

    public static void setBoolean(ItemStack stack, String tag, boolean b)
    {
        getNBT(stack).setBoolean(tag, b);
    }

    public static void setByte(ItemStack stack, String tag, byte b)
    {
        getNBT(stack).setByte(tag, b);
    }

    public static void setShort(ItemStack stack, String tag, short s)
    {
        getNBT(stack).setShort(tag, s);
    }

    public static void setInt(ItemStack stack, String tag, int i)
    {
        getNBT(stack).setInteger(tag, i);
    }

    public static void setLong(ItemStack stack, String tag, long l)
    {
        getNBT(stack).setLong(tag, l);
    }

    public static void setFloat(ItemStack stack, String tag, float f)
    {
        getNBT(stack).setFloat(tag, f);
    }

    public static void setDouble(ItemStack stack, String tag, double d)
    {
        getNBT(stack).setDouble(tag, d);
    }

    public static void setCompound(ItemStack stack, String tag, NBTTagCompound cmp)
    {
        getNBT(stack).setTag(tag, cmp);
    }

    public static void setString(ItemStack stack, String tag, String s)
    {
        getNBT(stack).setString(tag, s);
    }

    public static void setList(ItemStack stack, String tag, NBTTagList list)
    {
        getNBT(stack).setTag(tag, list);
    }

    public static void setIntArray(ItemStack stack, String tag, int[] intArray)
    {
        getNBT(stack).setIntArray(tag, intArray);
    }

    public static void setColor(ItemStack stack, Color color)
    {
        setInt(stack, Cuisine.MODID + ":Color", color.getRGB());
    }

    public static void removeTag(ItemStack stack, String tag)
    {
        getNBT(stack).removeTag(tag);
        if (getNBT(stack).isEmpty())
        {
            stack.setTagCompound(null);
        }
    }

    public static boolean verifyExistence(ItemStack stack, String tagName)
    {
        return !stack.isEmpty() && hasNBT(stack) && getNBT(stack).hasKey(tagName);
    }

    public static boolean getBoolean(ItemStack stack, String tag, boolean defaultExpected)
    {
        return verifyExistence(stack, tag) ? getNBT(stack).getBoolean(tag) : defaultExpected;
    }

    public static byte getByte(ItemStack stack, String tag, byte defaultExpected)
    {
        return verifyExistence(stack, tag) ? getNBT(stack).getByte(tag) : defaultExpected;
    }

    public static short getShort(ItemStack stack, String tag, short defaultExpected)
    {
        return verifyExistence(stack, tag) ? getNBT(stack).getShort(tag) : defaultExpected;
    }

    public static int getInt(ItemStack stack, String tag, int defaultExpected)
    {
        return verifyExistence(stack, tag) ? getNBT(stack).getInteger(tag) : defaultExpected;
    }

    public static long getLong(ItemStack stack, String tag, long defaultExpected)
    {
        return verifyExistence(stack, tag) ? getNBT(stack).getLong(tag) : defaultExpected;
    }

    public static float getFloat(ItemStack stack, String tag, float defaultExpected)
    {
        return verifyExistence(stack, tag) ? getNBT(stack).getFloat(tag) : defaultExpected;
    }

    public static double getDouble(ItemStack stack, String tag, double defaultExpected)
    {
        return verifyExistence(stack, tag) ? getNBT(stack).getDouble(tag) : defaultExpected;
    }

    public static NBTTagCompound getCompound(ItemStack stack, String tag, boolean nullifyOnFail)
    {
        return verifyExistence(stack, tag) ? getNBT(stack).getCompoundTag(tag)
                : nullifyOnFail ? null : new NBTTagCompound();
    }

    public static String getString(ItemStack stack, String tag, String defaultExpected)
    {
        return verifyExistence(stack, tag) ? getNBT(stack).getString(tag) : defaultExpected;
    }

    public static NBTTagList getList(ItemStack stack, String tag, int objtype, boolean nullifyOnFail)
    {
        return verifyExistence(stack, tag) ? getNBT(stack).getTagList(tag, objtype)
                : nullifyOnFail ? null : new NBTTagList();
    }

    public static int[] getIntArray(ItemStack stack, String tag, int[] defaultExpected)
    {
        return verifyExistence(stack, tag) ? getNBT(stack).getIntArray(tag) : defaultExpected;
    }

    public static Color getColor(ItemStack stack)
    {
        return new Color(getInt(stack, Cuisine.MODID + ":Color", -1));
    }

}
