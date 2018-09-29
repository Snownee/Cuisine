package snownee.cuisine.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import snownee.kiwi.potion.PotionMod;

public class PotionDispersal extends PotionMod
{
    public PotionDispersal(String name, int icon)
    {
        super(name, true, icon, false, 0xCCB89C, 0, true);
    }

    @Override
    public void performEffect(EntityLivingBase living, int amplifier)
    {
        if (living.isEntityUndead() && living.getHealth() > 1.0F)
        {
            living.attackEntityFrom(DamageSource.MAGIC, 1.0F);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier)
    {
        return duration % 25 == 0;
    }
}
