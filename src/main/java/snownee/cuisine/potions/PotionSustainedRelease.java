package snownee.cuisine.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import snownee.kiwi.potion.PotionMod;

public class PotionSustainedRelease extends PotionMod
{

    public PotionSustainedRelease(String name)
    {
        super(name, false, -1, false, 0xF82423, 20, false, false);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier)
    {
        if (entity instanceof EntityPlayer)
        {
            ((EntityPlayer) entity).getFoodStats().addStats(1, amplifier + 2);
        }
    }

}
