package snownee.cuisine.api;

import java.util.Locale;

public enum IngredientTrait
{
    CRISP(false),
    AROMATIC(false),
    GELATINOUS(false),
    STICKY(false),
    JUICY(false),
    REFRESHING(false),
    OILY(true),
    PLAIN(true),
    UNDERCOOKED(true),
    OVERCOOKED(true);

    /**
     * IngredientCharacteristics 所有元素组成的集合，用于避免 Enum.values() 的开销.
     */
    public static final IngredientTrait[] VALUES = IngredientTrait.values();

    private final boolean isBad;
    private final String translationKey;

    IngredientTrait(boolean isBad)
    {
        this.isBad = isBad;
        this.translationKey = "cuisine.ingredient.trait." + this.name().toLowerCase(Locale.ROOT);
    }

    public String getTranslationKey()
    {
        return translationKey;
    }

    public boolean isBad()
    {
        return isBad;
    }
}
