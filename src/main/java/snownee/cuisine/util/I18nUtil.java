package snownee.cuisine.util;

import net.minecraft.client.resources.I18n;
import snownee.cuisine.Cuisine;

public class I18nUtil
{
    public static String getFullKey(String key)
    {
        return Cuisine.MODID + "." + key;
    }

    public static boolean canTranslate(String key)
    {
        return I18n.hasKey(getFullKey(key));
    }

    public static String translate(String key, Object... parameters)
    {
        return I18n.format(getFullKey(key), parameters);
    }

    public static String translate(String key)
    {
        return I18n.format(getFullKey(key));
    }

    public static String translateWithEscape(String key, Object... parameters)
    {
        return translate(key, parameters).replaceAll("\\\\n", "\n");
    }
}
