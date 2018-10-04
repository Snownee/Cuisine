package snownee.cuisine.internal;

/**
 * Holder of many secret constants that are related to implementation
 * of Cuisine API.
 */
public interface CuisineSharedSecrets
{
    // Snownee: More data keys?

    String KEY_INGREDIENT_LIST = "ingredients";
    String KEY_SEASONING_LIST = "seasonings";
    String KEY_EFFECT_LIST = "effects";

    String KEY_MATERIAL = "material";
    String KEY_SPICE = "spice";

    String KEY_FORM = "form";
    String KEY_QUANTITY = "size";
    String KEY_TRAITS = "characteristics"; // It's not "traits" due to backward compatibility
    String KEY_SERVES = "durability";
    String KEY_USE_DURATION = "useDuration";

}
