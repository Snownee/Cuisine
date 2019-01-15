package snownee.cuisine.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;

@EventBusSubscriber(modid = Cuisine.MODID, value = Side.CLIENT)
public class DrunkHandler
{
    private static float scale = 0;
    private static boolean reverse = false;

    @SubscribeEvent
    public static void handleCamera(CameraSetup event)
    {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        PotionEffect effect = player.getActivePotionEffect(CuisineRegistry.DRUNK);
        if (effect != null && effect.getAmplifier() > 1)
        {
            if (scale == 0)
            {
                reverse = !reverse;
            }
            scale += event.getRenderPartialTicks() / 1000;
            if (scale > 0.2F)
            {
                scale = 0.2F;
            }
        }
        else
        {
            scale -= event.getRenderPartialTicks() / 1000;
            if (scale < 0)
            {
                scale = 0;
            }
        }
        if (scale > 0)
        {
            float rotation = Minecraft.getSystemTime() * 0.01F;
            GlStateManager.rotate(rotation, 0.0F, 1.0F, 1.0F);
            GlStateManager.scale(1 / (1 + scale), 1.0F, 1.0F);
            GlStateManager.rotate(-rotation, 0.0F, 1.0F, 1.0F);

            int factor = reverse ? 1 : -1;
            player.rotationYaw += factor * MathHelper.cos((float) (Minecraft.getSystemTime() % 5000 / 2500F * Math.PI)) * event.getRenderPartialTicks() * scale * 10;
            player.rotationPitch += MathHelper.sin((float) (Minecraft.getSystemTime() % 5000 / 2500F * Math.PI)) * event.getRenderPartialTicks() * scale * 3;
        }
    }
}
