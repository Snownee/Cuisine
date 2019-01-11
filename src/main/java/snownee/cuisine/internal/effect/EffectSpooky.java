package snownee.cuisine.internal.effect;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.prefab.DefaultTypes;
import snownee.cuisine.api.prefab.SimpleEffectImpl;

public class EffectSpooky extends SimpleEffectImpl
{

    public EffectSpooky()
    {
        super("spooky", 0x5931a0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEaten(ItemStack stack, EntityPlayer player, CompositeFood food, List<Ingredient> ingredients, EffectCollector collector)
    {
        if (player.world.isRemote)
        {
            return;
        }
        List<Potion> potions = ForgeRegistries.POTIONS.getValues();
        Potion potion;
        do
        {
            potion = potions.get(player.world.rand.nextInt(potions.size()));
        }
        while (potion.isInstant());
        collector.addEffect(DefaultTypes.POTION, new PotionEffect(potion, 600, Math.min(ingredients.size() - 1, 2)));
    }

}
