package snownee.cuisine.api;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

/**
 * Implementing this on an implementation of Item will provides ability to
 * determine CookingStrategy based on actual ItemStack. Things like
 * energy-powered cooking tools may want implement this.
 */
public interface CookingStrategyProvider
{
    /**
     * Get an instance of {@link CookingStrategy} based on the actual item
     * instance. In case where returning {@code null} is demanded, use
     * {@link CookingStrategy#identity()} as return value.
     *
     * @param stack The actual instance of item that implements this interface
     * @return An instance of {@link CookingStrategy}.
     */
    @Nonnull
    CookingStrategy getCookingStrategy(ItemStack stack);
}
