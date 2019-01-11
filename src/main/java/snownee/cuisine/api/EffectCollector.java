package snownee.cuisine.api;

import net.minecraft.entity.player.EntityPlayer;

public interface EffectCollector
{
    void apply(CompositeFood food, EntityPlayer player);

    <T> void addEffect(EffectType<T> type, T effect);

    default <T> void addEffects(EffectType<T> type, T... effects)
    {
        for (T effect : effects)
        {
            addEffect(type, effect);
        }
    }

    default <T> T getEffect(EffectType<T> type)
    {
        return null;
    }

    <T> void clear(EffectType<T> type);
}
