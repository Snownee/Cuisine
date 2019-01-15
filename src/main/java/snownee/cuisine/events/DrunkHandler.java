package snownee.cuisine.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;

@EventBusSubscriber(modid = Cuisine.MODID, value = Side.CLIENT)
public class DrunkHandler
{
    @SubscribeEvent
    public static void name(CameraSetup event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        PotionEffect effect = player.getActivePotionEffect(CuisineRegistry.DRUNK);
        if (effect != null && effect.getAmplifier() > 0)
        {
            //            System.out.println(player.rotationYaw - player.prevRotationYaw);
            // player.rotationYaw += effect.getAmplifier() * event.getRenderPartialTicks();
        }
    }
}
