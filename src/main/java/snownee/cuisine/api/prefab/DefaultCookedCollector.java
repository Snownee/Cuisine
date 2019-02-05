package snownee.cuisine.api.prefab;

import net.minecraft.entity.player.EntityPlayer;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.EffectType;

public class DefaultCookedCollector implements EffectCollector
{
    protected float durationModifier = 0F;
    protected int serveAmount = 0;

    public DefaultCookedCollector(int defaultServeAmount)
    {
        this.serveAmount = defaultServeAmount;
    }

    @Override
    public void apply(CompositeFood food, EntityPlayer player)
    {
        food.setUseDurationModifier(1 + durationModifier);
        food.setMaxServes(serveAmount);
        food.setServes(serveAmount);
    }

    @Override
    public <T> void addEffect(EffectType<T> type, T effect)
    {
        if (type == DefaultTypes.USE_DURATION_MODIFIER)
        {
            durationModifier += (Float) effect;
        }
        if (type == DefaultTypes.SERVE_AMOUNT)
        {
            serveAmount += (Integer) effect;
        }
    }

    @Override
    public <T> T getEffect(EffectType<T> type)
    {
        if (type == DefaultTypes.USE_DURATION_MODIFIER)
        {
            return DefaultTypes.USE_DURATION_MODIFIER.cast(durationModifier);
        }
        if (type == DefaultTypes.SERVE_AMOUNT)
        {
            return DefaultTypes.SERVE_AMOUNT.cast(serveAmount);
        }
        return null;
    }

    @Override
    public <T> void clear(EffectType<T> type)
    {
        if (type == DefaultTypes.USE_DURATION_MODIFIER)
        {
            durationModifier = 0F;
        }
    }

}
