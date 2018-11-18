package snownee.cuisine.server;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import snownee.cuisine.CuisineSidedProxy;

public final class CuisineServerProxy extends CuisineSidedProxy
{

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * Since physical server is not responsible for rendering at all, this
     * implementation will always return {@code null}.
     *
     * @return null, always
     */
    @Override
    public final IAnimationStateMachine loadAnimationStateMachine(ResourceLocation identifier, ImmutableMap<String, ITimeValue> parameters)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * On physical server, this method is delegate to the deprecated
     * {@code net.minecraft.util.text.translation.I18n}. Note that
     * that class no longer exists since Minecraft 1.13 Release; as such,
     * this method will return {@code translationKey} verbatim when it is
     * ported to Minecraft 1.13 Release or onward.
     */
    @Override
    @SuppressWarnings("deprecation") // net.minecraft.util.text.translation.I18n is deprecated.
    public final String translate(String translationKey)
    {
        return I18n.translateToLocal(translationKey);
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * On physical server, this method is delegate to the deprecated
     * {@code net.minecraft.util.text.translation.I18n}. Note that
     * that class no longer exists since Minecraft 1.13 Release; as such,
     * this method will return {@code translationKey} verbatim when it is
     * ported to Minecraft 1.13 Release or onward.
     */
    @Override
    @SuppressWarnings("deprecation") // net.minecraft.util.text.translation.I18n is deprecated.
    public final String translate(String translationKey, Object... params)
    {
        return I18n.translateToLocalFormatted(translationKey, params);
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * On physical server, this method is delegate to the deprecated
     * {@code net.minecraft.util.text.translation.I18n}. Note that
     * that class no longer exists since Minecraft 1.13 Release; as such,
     * this method will always return {@code false} when it is ported to
     * Minecraft 1.13 Release or onward.
     */
    @Override
    @SuppressWarnings("deprecation")
    public final boolean canTranslate(String translationKey)
    {
        return I18n.canTranslate(translationKey);
    }
}
