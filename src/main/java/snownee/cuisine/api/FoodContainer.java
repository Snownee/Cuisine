package snownee.cuisine.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

/**
 * A {@code FoodContainer} is a mutable holder of single {@link CompositeFood}
 * instance.
 */
public interface FoodContainer
{
    @Nullable
    CompositeFood get();

    void set(@Nullable CompositeFood newFoodInstance);

    /**
     * Return an instance of {@link ItemStack} that represents an "empty
     * food container".
     * @param current the current ItemStack instance that has this FoodContainer
     * @return A container item
     */
    @Nonnull
    ItemStack getEmptyContainer(ItemStack current);
}
