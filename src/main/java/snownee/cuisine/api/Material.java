package snownee.cuisine.api;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.util.math.MathHelper;

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

    default int getColorCode(int doneness)
    {
        if (doneness < 100)
        {
            return mixColor(getRawColorCode(), getCookedColorCode(), doneness / 100f);
        }
        else
        {
            return mixColor(getCookedColorCode(), 0, (doneness - 100) / 100f);
        }
    }

    default boolean hasGlowingOverlay(Ingredient ingredient)
    {
        return false;
    }

    int getInitialWaterValue();

    int getInitialOilValue();

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

    default void onMade(final CompositeFood.Builder<?> dish, final Ingredient ingredient, final CookingVessel vessel, final EffectCollector collector)
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

    public static int mixColor(int color1, int color2, float weight)
    {
        if (weight <= 0)
        {
            return color1;
        }
        else if (weight >= 1)
        {
            return color2;
        }
        else
        {
            float a = color1 >> 24 & 255;
            float r = color1 >> 16 & 255;
            float g = color1 >> 8 & 255;
            float b = color1 & 255;
            float a1 = color2 >> 24 & 255;
            float r1 = color2 >> 16 & 255;
            float g1 = color2 >> 8 & 255;
            float b1 = color2 & 255;
            a += (a1 - a) * weight;
            r += (r1 - r) * weight;
            g += (g1 - g) * weight;
            b += (b1 - b) * weight;
            a = MathHelper.clamp(a, 0, 255);
            r = MathHelper.clamp(r, 0, 255);
            g = MathHelper.clamp(g, 0, 255);
            b = MathHelper.clamp(b, 0, 255);
            return (int) a << 24 | (int) r << 16 | (int) g << 8 | (int) b;
        }
    }
}
