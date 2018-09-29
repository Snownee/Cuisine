package snownee.cuisine.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface Spice
{

    void addFlavorTo(CompositeFood dish, int quantity);

    String getID();

    String getTranslationKey();

    int getColorCode();

    default void onAddedInto(final CompositeFood dish, final CookingVessel vessel)
    {
        // NO-OP
    }

    default void onRemovedFrom(final CompositeFood dish)
    {
        // NO-OP
    }

    default void onConsumed(ItemStack stack, EntityPlayer player, World worldIn, Seasoning seasoning, EffectCollector collector)
    {
        // NO-OP
    }

    default void onCooked(final CompositeFood dish, final Seasoning seasoning, final CookingVessel vessel, final EffectCollector collector)
    {
        // NO-OP
    }
}
