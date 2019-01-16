package snownee.cuisine.internal.effect;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.prefab.DefaultTypes;
import snownee.cuisine.api.prefab.SimpleEffectImpl;

public class EffectRare extends SimpleEffectImpl
{

    public EffectRare()
    {
        super("rare", 0);
    }

    @Override
    public void onEaten(ItemStack stack, EntityPlayer player, CompositeFood food, List<Ingredient> ingredients, EffectCollector collector)
    {
        collector.addEffect(DefaultTypes.FOOD_LEVEL, 1);
    }

    @Override
    public boolean showInTooltips()
    {
        return false;
    }
}
