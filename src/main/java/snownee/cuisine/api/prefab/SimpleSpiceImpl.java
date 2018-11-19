package snownee.cuisine.api.prefab;

import snownee.cuisine.api.Seasoning;
import snownee.cuisine.api.Spice;

public class SimpleSpiceImpl implements Spice
{
    private final String id;
    private final int color;
    private final boolean liquid;

    public SimpleSpiceImpl(String id, int color, boolean liquid)
    {
        this.id = id;
        this.color = color;
        this.liquid = liquid;
    }

    @Override
    public String getID()
    {
        return id;
    }

    @Override
    public int getColorCode()
    {
        return color;
    }

    @Override
    public String getTranslationKey()
    {
        return "cuisine.spice." + getID();
    }

    @Override
    public boolean isLiquid(Seasoning seasoning)
    {
        return liquid;
    }
}
