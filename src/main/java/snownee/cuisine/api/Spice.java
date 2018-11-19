package snownee.cuisine.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface Spice
{

    String getID();

    String getTranslationKey();

    int getColorCode();

    boolean isLiquid(Seasoning seasoning);

    default void onAddedInto(final CompositeFood.Builder<?> dish, final CookingVessel vessel)
    {
        // NO-OP
    }

    default void onRemovedFrom(final CompositeFood.Builder<?> dish)
    {
        // NO-OP
    }

    /**
     * Callback method that will be invoked when a player directly consumes seasoning object.
     *
     * @param spiceContainerItem the ItemStack instance that functions like a container of
     *                           seasoning object
     * @param consumer           the player who consumed the seasoning object
     * @param world              the world where the player is in
     * @param actualContent      the form in which the seasoning object exists; in Cuisine,
     *                           the only two known possibilities are ItemStack and FluidStack
     *
     * @deprecated Work in progress, do NOT call it even if it is harmless
     */
    @Deprecated
    default void onConsumedDirectly(ItemStack spiceContainerItem, EntityPlayer consumer, World world, Object actualContent)
    {
        // Default to no-op
    }

    default void onConsumed(ItemStack stack, EntityPlayer player, World worldIn, Seasoning seasoning, EffectCollector collector)
    {
        // NO-OP
    }

    default void onCooked(final CompositeFood.Builder<?> dish, final Seasoning seasoning, final CookingVessel vessel, final EffectCollector collector)
    {
        // NO-OP
    }
}
