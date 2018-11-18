package snownee.cuisine.internal.effect;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.prefab.DefaultTypes;
import snownee.cuisine.api.prefab.SimpleEffectImpl;

public class EffectPotions extends SimpleEffectImpl
{
    List<PotionEffect> effects = new ArrayList<>(4);

    public EffectPotions(String name)
    {
        super(name, 0);
    }

    public EffectPotions addPotionEffect(PotionEffect effect)
    {
        effects.add(effect);
        return this;
    }

    @Override
    public void onEaten(ItemStack stack, EntityPlayer player, @Nullable CompositeFood food, @Nullable Ingredient ingredient, EffectCollector collector)
    {
        double size = ingredient == null ? 1 : ingredient.getSize();
        int modifier = ingredient == null || ingredient.getForm().ordinal() > 3 ? 1 : 2;
        // TODO: Fine tuning
        for (PotionEffect effect : effects)
        {
            collector.addEffect(DefaultTypes.POTION, new PotionEffect(effect.getPotion(), Math.max(0, (int) (effect.getDuration() * size * 4 / modifier)), effect.getAmplifier() + modifier - 1, effect.getIsAmbient(), effect.doesShowParticles()));
        }
    }

    @Override
    public int getColor()
    {
        return PotionUtils.getPotionColorFromEffectList(effects);
    }
}
