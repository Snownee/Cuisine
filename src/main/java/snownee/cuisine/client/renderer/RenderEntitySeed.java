package snownee.cuisine.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import snownee.cuisine.entities.EntitySeed;

public class RenderEntitySeed extends RenderSnowball<EntitySeed>
{
    public static final IRenderFactory<EntitySeed> FACTORY = new Factory();

    public RenderEntitySeed(RenderManager renderManagerIn, RenderItem itemRendererIn)
    {
        super(renderManagerIn, Items.WHEAT_SEEDS, itemRendererIn);
    }

    @Override
    public ItemStack getStackToRender(EntitySeed entityIn)
    {
        return entityIn.getItem();
    }

    private static class Factory implements IRenderFactory<EntitySeed>
    {
        @Override
        public Render<? super EntitySeed> createRenderFor(RenderManager manager)
        {
            return new RenderEntitySeed(manager, Minecraft.getMinecraft().getRenderItem());
        }
    }
}
