package snownee.cuisine.util;

import net.minecraftforge.client.MinecraftForgeClient;
import snownee.cuisine.Cuisine;

import java.text.MessageFormat;

public class I18nUtil
{
    public static String getFullKey(String key)
    {
        return Cuisine.MODID + "." + key;
    }

    public static boolean canTranslate(String key)
    {
        return Cuisine.sidedDelegate.canTranslate(getFullKey(key));
    }

    /**
     * Translate using given parameters as formatting arguments (see {@link
     * String#format(String, Object...)}.
     *
     * @param key translation key
     * @param parameters format arguments
     *
     * @return translated text
     *
     * @see java.util.Formatter
     * @see String#format(String, Object...)
     */
    public static String translate(String key, Object... parameters)
    {
        return Cuisine.sidedDelegate.translate(getFullKey(key), parameters);
    }

    /**
     * Translate specified string directly.
     *
     * @param key translation key
     *
     * @return translated text
     */
    public static String translate(String key)
    {
        return Cuisine.sidedDelegate.translate(getFullKey(key));
    }

    /**
     * Translate using given parameters as formatting arguments (see {@link
     * String#format(String, Object...)}, with all literal {@code \n} being
     * replaced with new line character.
     *
     * @param key translation key
     * @param parameters format arguments
     *
     * @return translated text
     *
     * @see #translate(String, Object...)
     */
    public static String translateWithEscape(String key, Object... parameters)
    {
        return translate(key, parameters).replaceAll("\\\\n", "\n");
    }

    /**
     * Translate using given parameters as formatting arguments. Differing
     * from {@link #translate(String, Object...)}, this method uses
     * {@link MessageFormat} to properly format the message.
     *
     * @param key translation key
     * @param args format arguments
     *
     * @return translated text
     *
     * @see MessageFormat
     */
    public static String translateWithFormat(String key, Object... args)
    {
        return new MessageFormat(translate(key), MinecraftForgeClient.getLocale()).format(args);
    }
}
