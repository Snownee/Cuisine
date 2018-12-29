package snownee.cuisine.api.prefab;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import snownee.cuisine.api.Seasoning;
import snownee.cuisine.api.Spice;

public class SimpleSpiceImpl implements Spice
{
    private final String id;
    private final int color;
    private final boolean liquid;
    private final Set<String> keywords;

    public SimpleSpiceImpl(String id, int color, boolean liquid)
    {
        this(id, color, liquid, Collections.EMPTY_SET);
    }

    public SimpleSpiceImpl(String id, int color, boolean liquid, Set<String> keywords)
    {
        this.id = id;
        this.color = color;
        this.liquid = liquid;
        this.keywords = keywords;
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

    @Override
    public Collection<String> getKeywords()
    {
        return keywords;
    }
}
