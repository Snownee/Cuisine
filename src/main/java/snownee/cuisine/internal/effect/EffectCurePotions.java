package snownee.cuisine.internal.effect;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.prefab.SimpleEffectImpl;

public class EffectCurePotions extends SimpleEffectImpl
{

    public EffectCurePotions(String name)
    {
        super(name, 0xC1FFC1);
    }

    @Override
    public void onEaten(ItemStack stack, EntityPlayer player, CompositeFood food, Ingredient[] ingredients, EffectCollector collector)
    {
        player.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
    }
}
