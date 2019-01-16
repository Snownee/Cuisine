package snownee.cuisine.internal.effect;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Form;
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
    public void onEaten(ItemStack stack, EntityPlayer player, CompositeFood food, List<Ingredient> ingredients, EffectCollector collector)
    {
        double size = ingredients.size();
        int modifier = 0;
        int count = 0;
        for (Ingredient ingredient : ingredients)
        {
            if (ingredient == null)
            {
                count += Form.JUICE.ordinal();
            }
            else
            {
                count += ingredient.getForm().ordinal();
            }
        }
        if (count / size > 4)
        {
            modifier = 1;
        }
        // TODO: Fine tuning
        for (PotionEffect effect : effects)
        {
            collector.addEffect(DefaultTypes.POTION, new PotionEffect(effect.getPotion(), Math.max(0, (int) (effect.getDuration() * size * 2 / (modifier + 1) / 4)), effect.getAmplifier() + modifier, effect.getIsAmbient(), effect.doesShowParticles()));
        }
    }

    @Override
    public int getColor()
    {
        return PotionUtils.getPotionColorFromEffectList(effects);
    }
}
