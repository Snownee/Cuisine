package snownee.cuisine.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.List;

public interface Effect
{

    Comparator<Effect> PRIORITY_COMPARATOR = Comparator.comparingInt(Effect::getPriority);

    Comparator<Effect> INVERSE_PRIORITY_COMPARATOR = PRIORITY_COMPARATOR.reversed();

    void onEaten(ItemStack stack, EntityPlayer player, CompositeFood compositeFood, List<Ingredient> ingredients, EffectCollector collector);

    int getPriority();

    String getID();

    String getName();

    int getColor();

    String getDescription();

    default boolean showInTooltips()
    {
        return true;
    }
}
