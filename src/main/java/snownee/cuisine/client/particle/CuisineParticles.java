package snownee.cuisine.client.particle;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;

@SideOnly(Side.CLIENT)
// @EventBusSubscriber(modid = Cuisine.MODID, value = Side.CLIENT)
public class CuisineParticles
{
    public static final ResourceLocation PARTICLES = new ResourceLocation(Cuisine.MODID, "misc/particles");

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent event)
    {
        event.getMap().registerSprite(PARTICLES);
    }
}
