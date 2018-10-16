package snownee.cuisine.internal.effect;

import snownee.cuisine.api.prefab.SimpleEffectImpl;

public class EffectHarmony extends SimpleEffectImpl
{

    public EffectHarmony()
    {
        super("harmony", 0xFFC0CB);
    }

    @Override
    public int getPriority()
    {
        return 5;
    }

}
