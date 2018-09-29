package snownee.cuisine.api;

import javax.annotation.Nullable;

public enum MaterialCategory
{

    GRAIN("grain"),

    VEGETABLES("veggie"), // This includes seaweed and the alike.

    FRUIT("fruit"),

    FISH("fish"),

    MEAT(), // You cant tell whether it is raw or cooked

    PROTEIN(), // Dairy and things like egg which contains good protein but doesn't count as meat

    NUT("nut"),

    SEAFOOD("seafood"), // Things that are from sea but are not fish, for example calms and shrimps

    SUPERNATURAL(), // Things like enderperal and chrous fruit

    UNKNOWN(); // Things that cannot even be categorized

    private final String ore;

    MaterialCategory()
    {
        this(null);
    }

    MaterialCategory(@Nullable String ore)
    {
        this.ore = ore;
    }

    @Nullable
    public String getOreName()
    {
        return ore;
    }
}
