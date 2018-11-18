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

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * On physical client, this method is delegated to {@link
     * ModelLoaderRegistry#loadASM(ResourceLocation, ImmutableMap)}.
     */
    @Override
    public final IAnimationStateMachine loadAnimationStateMachine(ResourceLocation identifier, ImmutableMap<String, ITimeValue> parameters)
    {
        return ModelLoaderRegistry.loadASM(identifier, parameters);
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * On physical client, this method is delegate to {@link
     * net.minecraft.client.resources.I18n}.
     */
    @Override
    public final String translate(String translationKey)
    {
        return I18n.format(translationKey);
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * On physical client, this method is delegate to {@link
     * net.minecraft.client.resources.I18n}.
     */
    @Override
    public final String translate(String translationKey, Object... params)
    {
        return I18n.format(translationKey, params);
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec
     * On physical client, this method is delegate to {@link
     * net.minecraft.client.resources.I18n}.
     */
    @Override
    public boolean canTranslate(String translationKey)
    {
        return I18n.hasKey(translationKey);
    }
}
