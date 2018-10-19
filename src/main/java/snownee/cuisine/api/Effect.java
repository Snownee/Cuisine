package snownee.cuisine.api;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Comparator;

public interface Effect
{

    Comparator<Effect> PRIORITY_COMPARATOR = Comparator.comparingInt(Effect::getPriority);

    Comparator<Effect> INVERSE_PRIORITY_COMPARATOR = PRIORITY_COMPARATOR.reversed();

    void onEaten(ItemStack stack, EntityPlayer player, CompositeFood compositeFood, @Nullable Ingredient ingredient, EffectCollector collector);

    int getPriority();

    String getID();

    String getName();

    @SideOnly(Side.CLIENT)
    int getColorForDisplay();

    String getDescription();

    default boolean showInTooltips()
    {
        return true;
    }
}
