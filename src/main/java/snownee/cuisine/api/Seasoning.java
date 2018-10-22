package snownee.cuisine.api;

import org.apache.commons.lang3.Validate;

public final class Seasoning
{
    private final Spice spice;
    private int quantity;

    public Seasoning(Spice spice)
    {
        this(spice, 1);
    }

    public Seasoning(Spice spice, int quantity)
    {
        this.spice = spice;
        this.quantity = quantity;
    }

    public boolean matchType(Seasoning another)
    {
        return this.spice == another.spice;
    }

    public void merge(Seasoning another)
    {
        Validate.isTrue(this.matchType(another));
        this.quantity += another.quantity;
    }

    public int getSize()
    {
        return this.quantity;
    }

    public Spice getSpice()
    {
        return spice;
    }

    public void decreaseSizeBy(int decrement)
    {
        this.quantity -= decrement;
    }
}
