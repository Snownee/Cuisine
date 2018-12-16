package snownee.cuisine.internal.effect;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.Loader;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.plugins.TANCompat;
import snownee.kiwi.Kiwi;

public class EffectHeatResistance extends EffectPotions
{

    public EffectHeatResistance(String name)
    {
        super(name);
        if (Loader.isModLoaded("toughasnails") && !Kiwi.isOptionalModuleLoaded(Cuisine.MODID, "toughasnails") && TANCompat.heat_resistance != null)
        {
            addPotionEffect(new PotionEffect(TANCompat.heat_resistance, 600));
        }
    }

    @Override
    public void onEaten(ItemStack stack, EntityPlayer player, CompositeFood food, Ingredient[] ingredients, EffectCollector collector)
    {
        player.extinguish();
        super.onEaten(stack, player, food, ingredients, collector);
    }

}
