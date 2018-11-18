package snownee.cuisine.server;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import snownee.cuisine.CuisineSidedProxy;

public final class CuisineServerProxy extends CuisineSidedProxy
{
    @Override
    public final IAnimationStateMachine loadAnimationStateMachine(ResourceLocation identifier, ImmutableMap<String, ITimeValue> parameters)
    {
        return null;
    }

    @Override
    @SuppressWarnings("deprecation") // net.minecraft.util.text.translation.I18n is deprecated.
    public final String translate(String translationKey)
    {
        return I18n.translateToLocal(translationKey);
    }

    @Override
    @SuppressWarnings("deprecation") // net.minecraft.util.text.translation.I18n is deprecated.
    public final String translate(String translationKey, Object... params)
    {
        return I18n.translateToLocalFormatted(translationKey, params);
    }
}
