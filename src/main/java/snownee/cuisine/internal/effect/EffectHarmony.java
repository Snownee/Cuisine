package snownee.cuisine.internal.effect;

import snownee.cuisine.api.prefab.SimpleEffectImpl;

public class EffectHarmony extends SimpleEffectImpl
{

    public EffectHarmony()
    {
        super("harmony");
    }

    @Override
    public int getPriority()
    {
        return 5;
    }

    @Override
    public int getColorForDisplay()
    {
        return 0xFFC0CB;
    }

}
