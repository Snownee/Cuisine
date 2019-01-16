package snownee.cuisine.internal.effect;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.prefab.DefaultTypes;
import snownee.cuisine.api.prefab.SimpleEffectImpl;

public class EffectSustainedRelease extends SimpleEffectImpl
{

    public EffectSustainedRelease()
    {
        super("sustained_release", 0xF82423);
    }

    @Override
    public void onEaten(ItemStack stack, EntityPlayer player, CompositeFood food, List<Ingredient> ingredients, EffectCollector collector)
    {
        int foodLevel = collector.getEffect(DefaultTypes.FOOD_LEVEL);
        collector.addEffect(DefaultTypes.FOOD_LEVEL, -foodLevel);
        if (foodLevel > 0)
        {
            player.addPotionEffect(new PotionEffect(CuisineRegistry.SUSTAINED_RELEASE, foodLevel * 100 - 1, 0, false, false));
        }
    }

    @Override
    public int getPriority()
    {
        return -5;
    }

}
