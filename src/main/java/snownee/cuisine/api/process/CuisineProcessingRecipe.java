package snownee.cuisine.api.process;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public interface CuisineProcessingRecipe
{

    /**
     * Determine whether the given array of inputs match this recipe object.
     * 
     * @return true if the input matches this recipe object
     */
    boolean matches(Object... inputs);

    /**
     * Determine whether the given Object is exactly equals to this recipe object.
     *
     * @implSpec
     * Generally, an implementation of <code>CuisineProcessingRecipe</code> may
     * choose to only consider about their inputs when dealing with equivalency.
     * In another word, recipes with same inputs but different outputs may overlap
     * due to this.
     *
     * @param another Another recipe object
     * @return <code>true</code> if and only if this recipe object is equivalent
     * to the given object, <code>false</code> otherwise.
     */
    @Override
    boolean equals(Object another);

    /**
     * Returns a {@link ResourceLocation} object that functions as a unique locator
     * of this recipe object.
     * @return a unique locator
     */
    @Nonnull ResourceLocation getIdentifier();
}
