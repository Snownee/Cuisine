package snownee.cuisine.api;

import java.util.EnumSet;
import java.util.Set;

public interface Material
{

    String getID();

    boolean isValidForm(Form form);

    /**
     * @return A set of forms where each of them makes isValidForm return true
     */
    EnumSet<Form> getValidForms();

    float getSaturationModifier();

    int getRawColorCode();

    int getCookedColorCode();

    default boolean hasGlowingOverlay(Ingredient ingredient)
    {
        return false;
    }

    int getInitialWaterValue();

    int getInitialOilValue();

    int getInitialHeatValue();

    default float getSaturationModifier(final Ingredient ingredient)
    {
        return this.getSaturationModifier();
    }

    default void onAddedInto(final CompositeFood.Builder<?> dish, final Ingredient ingredient, final CookingVessel vessel)
    {
        // NO-OP
    }

    default boolean canAddInto(final CompositeFood.Builder<?> dish, final Ingredient ingredient)
    {
        return true;
    }

    // Nullable dish for barbecue? Reply: that's a dish with just one (1) ingredient.
    default void onCooked(final CompositeFood.Builder<?> dish, final Ingredient ingredient, final CookingVessel vessel, final EffectCollector collector)
    {
        // NO-OP
    }

    default void onCrafted(final Ingredient ingredient)
    {
        if (this.isUnderCategoryOf(MaterialCategory.FRUIT) && this.isUnderCategoryOf(MaterialCategory.SUPERNATURAL) && this.isUnderCategoryOf(MaterialCategory.UNKNOWN))
        {
            ingredient.addTrait(IngredientTrait.UNDERCOOKED);
        }
    }

    boolean isUnderCategoryOf(MaterialCategory category);

    Set<MaterialCategory> getCategories();

    String getTranslationKey();
}
