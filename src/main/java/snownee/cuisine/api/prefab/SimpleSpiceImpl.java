package snownee.cuisine.api.prefab;

import snownee.cuisine.api.Spice;

public class SimpleSpiceImpl implements Spice
{
    private final String id;
    private final int color;

    public SimpleSpiceImpl(String id, int color)
    {
        this.id = id;
        this.color = color;
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
}
