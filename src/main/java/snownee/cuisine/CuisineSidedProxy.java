package snownee.cuisine;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

public abstract class CuisineSidedProxy
{

    /**
     * Load a {@link IAnimationStateMachine} instance from given location, with given
     * {@link ITimeValue time values list} applied.
     *
     * @param identifier the location of which the definition is stored
     * @param parameters the desired String-to-ITimeValue map that will be used by the state machine
     *
     * @return the animation state machine instance.
     */
    public abstract IAnimationStateMachine loadAnimationStateMachine(ResourceLocation identifier, ImmutableMap<String, ITimeValue> parameters);

    /**
     * Retrieve the translation of a string with the specified translation key.
     * The target language is determined by the language setting at the time
     * when the method is called.
     *
     * @implSpec
     * Default implementation assumes that there is no translation at all, and
     * will return the translation key verbatim.
     *
     * @param translationKey the key that associated to desired text.
     * @return The translated text according to current language setting.
     */
    public String translate(String translationKey)
    {
        return translationKey;
    }

    /**
     * Retrieve the translation of a string with the specified translation key,
     * with formatting parameters applied. The target language is determined by
     * the language setting at the time when the method is called.
     *
     * @implSpec
     * Default implementation assumes that there is no translation at all, and
     * will return the translation key verbatim.
     *
     * @param translationKey the key that associated to desired text.
     * @param params the parameters used by formatting
     *
     * @return The translated text according to current language setting.
     *
     * @see String#format(String, Object...)
     */
    public String translate(String translationKey, Object... params)
    {
        return translationKey;
    }

    /**
     * Determine whether there is a translation available for the given translation
     * key.
     *
     * @implSpec
     * Default implementation assumes that there is no translation at all, and
     * will always return false.
     *
     * @param fullKey The full traslation key string
     * @return true if there is an associated translation string; false otherwise.
     */
    public boolean canTranslate(String fullKey)
    {
        return false;
    }
}
