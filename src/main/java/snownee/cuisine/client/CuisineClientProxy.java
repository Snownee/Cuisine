package snownee.cuisine.client;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import snownee.cuisine.CuisineSidedProxy;

public final class CuisineClientProxy extends CuisineSidedProxy
{

    @Override
    public final IAnimationStateMachine loadAnimationStateMachine(ResourceLocation identifier, ImmutableMap<String, ITimeValue> parameters)
    {
        return ModelLoaderRegistry.loadASM(identifier, parameters);
    }

    @Override
    public final String translate(String translationKey)
    {
        return I18n.format(translationKey);
    }

    @Override
    public final String translate(String translationKey, Object... params)
    {
        return I18n.format(translationKey, params);
    }

    @Override
    public boolean canTranslate(String translationKey)
    {
        return I18n.hasKey(translationKey);
    }
}
