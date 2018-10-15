package snownee.cuisine.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
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

        FluidStack fluid = te.getFluidForRendering(partialTicks);
        ItemStack item = te.stacks.getStackInSlot(0);
        if (fluid == null && item.isEmpty())
        {
            return;
        }

        GlStateManager.pushMatrix();

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

        if (!item.isEmpty() && te.hasWorld())
        {
            GlStateManager.pushMatrix();

            GlStateManager.translate(0.5, 0.0625, 0.5);
            RenderItem renderItem = mc.getRenderItem();
            IBakedModel bakedModel = renderItem.getItemModelWithOverrides(item, te.getWorld(), null);
            if (bakedModel.isGui3d())
            {
                // Block
                GlStateManager.scale(0.4, 0.4, 0.4);
                GlStateManager.translate(0.2, 0, 0.2);
            }
            else
            {
                // Item
                GlStateManager.scale(0.5, 0.5, 0.5);
                GlStateManager.rotate(90, 1, 0, 0);
            }
            int max = item.getCount() == 1 ? 1 : (item.getCount() - 1) / (bakedModel.isGui3d() ? 16 : 8) + 1;
            for (int i = 0; i < max; i++)
            {
                if (bakedModel.isGui3d())
                {
                    // Block
                    double translation = i % 2 == 0 ? -0.4 : 0.4;
                    GlStateManager.translate(translation, 0.2, translation);
                    GlStateManager.rotate(70, 0, 1, 0);
                }
                else
                {
                    // Item
                    double translation = i % 2 == 0 ? -0.1 : 0.1;
                    GlStateManager.translate(translation, translation, -0.1);
                    GlStateManager.rotate(70, 0, 0, 1);
                }
                renderItem.renderItem(item, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();
        }

        if (fluid != null)
        {

            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.pushMatrix();
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

            double height = 0.0625 + 0.437 * ((double) fluid.amount / te.tank.getCapacity());

            buffer.pos(0.0625, height, 0.0625).color(r, g, b, a).tex(still.getMinU(), still.getMinV()).lightmap(lx, ly).endVertex();
            buffer.pos(0.0625, height, 0.9375).color(r, g, b, a).tex(still.getMinU(), still.getMaxV()).lightmap(lx, ly).endVertex();
            buffer.pos(0.9375, height, 0.9375).color(r, g, b, a).tex(still.getMaxU(), still.getMaxV()).lightmap(lx, ly).endVertex();
            buffer.pos(0.9375, height, 0.0625).color(r, g, b, a).tex(still.getMaxU(), still.getMinV()).lightmap(lx, ly).endVertex();

            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }
}
