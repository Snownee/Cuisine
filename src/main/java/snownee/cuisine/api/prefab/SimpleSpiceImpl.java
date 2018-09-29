package snownee.cuisine.api.prefab;

import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
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
    public void addFlavorTo(CompositeFood dish, int quantity)
    {
        // "Dish 'foo' is added with spice 'bar' of (size) baz
        Cuisine.logger.debug("Dish '{}' is added with spice '{}' of {}", dish, this, quantity);
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
