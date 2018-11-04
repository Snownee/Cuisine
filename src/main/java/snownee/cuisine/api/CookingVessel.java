package snownee.cuisine.api;

import java.util.Optional;

import net.minecraft.item.ItemStack;

/**
 * A {@code CookingVessel} is something that has ability to hold a
 * {@link CompositeFood.Builder}.
 */
public interface CookingVessel
{
    Optional<ItemStack> serve();
}
