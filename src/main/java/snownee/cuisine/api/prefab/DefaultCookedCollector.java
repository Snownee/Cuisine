package snownee.cuisine.api.prefab;

import net.minecraft.entity.player.EntityPlayer;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.EffectType;

public class DefaultCookedCollector implements EffectCollector
{
    protected float durationModifier = 0F;

    @Override
    public void apply(CompositeFood food, EntityPlayer player)
    {
        food.setUseDurationModifier(1 + durationModifier);
    }

    @Override
    public <T> void addEffect(EffectType<T> type, T effect)
    {
        if (type == DefaultTypes.USE_DURATION_MODIFIER)
        {
            durationModifier += (Float) effect;
        }
    }

    @Override
    public <T> void clear(EffectType<T> type)
    {
        durationModifier = 0F;
    }

}
