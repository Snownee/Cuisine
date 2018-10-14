package snownee.cuisine.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.tiles.TileBasin;

public class TESRBasin extends TileEntitySpecialRenderer<TileBasin>
{
    @Override
    public void render(TileBasin te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        FluidStack fluid = te.getCurrentFluidContent();
        ItemStack item = te.stacks.getStackInSlot(0);
        if (fluid == null && item.isEmpty())
        {
            return;
        }

        GlStateManager.pushMatrix();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (Minecraft.isAmbientOcclusionEnabled())
        {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        }
        else
        {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        GlStateManager.translate(x, y, z);

        Minecraft mc = Minecraft.getMinecraft();

        if (fluid != null)
        {
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
            if (still == null)
            {
                still = mc.getTextureMapBlocks().getMissingSprite();
            }

            int brightness = mc.world.getCombinedLight(te.getPos(), fluid.getFluid().getLuminosity(fluid));
            int lx = brightness >> 0x10 & 0xFFFF;
            int ly = brightness & 0xFFFF;

            int color = fluid.getFluid().getColor(fluid);
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            int a = color >> 24 & 0xFF;

            // Test codes
            float minU = still.getInterpolatedU(0);
            float maxU = still.getInterpolatedU(0);
            float minV = still.getInterpolatedV(16);
            float maxV = still.getInterpolatedV(16);

            double height = 0.125 + 0.37 * ((double) fluid.amount / te.tank.getCapacity());
            
            // Test codes
            buffer.pos(0, height, 0).tex(minU, minV).lightmap(lx, ly).color(r, g, b, a).endVertex();
            buffer.pos(0, height, 1).tex(minU, maxV).lightmap(lx, ly).color(r, g, b, a).endVertex();
            buffer.pos(1, height, 1).tex(maxU, maxV).lightmap(lx, ly).color(r, g, b, a).endVertex();
            buffer.pos(1, height, 0).tex(maxU, minV).lightmap(lx, ly).color(r, g, b, a).endVertex();
            //            buffer.pos(0.1875, height, 0.1875).tex(still.getMinU(), still.getMinV()).lightmap(lx, ly).color(r, g, b, a).endVertex();
            //            buffer.pos(0.8125, height, 0.1875).tex(still.getMinU(), still.getMaxV()).lightmap(lx, ly).color(r, g, b, a).endVertex();
            //            buffer.pos(0.8125, height, 0.8125).tex(still.getMaxU(), still.getMaxV()).lightmap(lx, ly).color(r, g, b, a).endVertex();
            //            buffer.pos(0.1875, height, 0.8125).tex(still.getMaxU(), still.getMinV()).lightmap(lx, ly).color(r, g, b, a).endVertex();

            tessellator.draw();
        }

        if (!item.isEmpty())
        {

        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }
}
