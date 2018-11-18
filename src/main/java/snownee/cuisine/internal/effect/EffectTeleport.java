package snownee.cuisine.internal.effect;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.prefab.SimpleEffectImpl;

public class EffectTeleport extends SimpleEffectImpl
{

    public EffectTeleport()
    {
        super("teleport", 0);
    }

    @Override
    public int getColor()
    {
        return CulinaryHub.CommonMaterials.CHORUS_FRUIT.getRawColorCode();
    }

    @Override
    public void onEaten(ItemStack stack, EntityPlayer player, CompositeFood food, @Nullable Ingredient ingredient, EffectCollector collector)
    {
        World world = player.getEntityWorld();
        if (!world.isRemote)
        {
            double d0 = player.posX;
            double d1 = player.posY;
            double d2 = player.posZ;

            for (int i = 0; i < 16; ++i)
            {
                double d3 = player.posX + (player.getRNG().nextDouble() - 0.5D) * 16.0D * ingredient.getSize();
                double d4 = MathHelper.clamp(player.posY + (player.getRNG().nextInt(16) - 8), 0.0D, world.getActualHeight() - 1);
                double d5 = player.posZ + (player.getRNG().nextDouble() - 0.5D) * 16.0D * ingredient.getSize();

                if (player.isRiding())
                {
                    player.dismountRidingEntity();
                }

                if (player.attemptTeleport(d3, d4, d5))
                {
                    world.playSound(null, d0, d1, d2, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    player.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                    break;
                }
            }
        }
    }

}
