package snownee.cuisine.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.fluids.CuisineFluids;
import snownee.cuisine.tiles.TileDrinkroTank;

public class TESRDrinkroTank extends TileEntitySpecialRenderer<TileDrinkroTank>
{
    @Override
    public void render(TileDrinkroTank te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        int meta = 0;
        if (te.hasWorld())
        {
            meta = te.getBlockMetadata() & 3;
        }
        int rot = 0;
        if (meta == 0)
        {
            if (!CuisineConfig.GENERAL.alwaysRenderDrinkro && z > 0)
            {
                return;
            }
            rot = 180;
        }
        else if (meta == 1)
        {
            if (!CuisineConfig.GENERAL.alwaysRenderDrinkro && x < -1)
            {
                return;
            }
            rot = 90;
        }
        else if (meta == 2)
        {
            if (!CuisineConfig.GENERAL.alwaysRenderDrinkro && z < -1)
            {
                return;
            }
        }
        else if (meta == 3)
        {
            if (!CuisineConfig.GENERAL.alwaysRenderDrinkro && x > 0)
            {
                return;
            }
            rot = -90;
        }

        if (te.builder == null)
        {
            return;
        }
        int itemCount = 0;
        if (te.inventory != null)
        {
            for (int i = 0; i < te.inventory.getSlots(); ++i)
            {
                if (!te.inventory.getStackInSlot(i).isEmpty())
                {
                    ++itemCount;
                }
            }
        }

        double size = te.builder.getCurrentSize();
        if (itemCount == 0 && size == 0)
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

        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.rotate(rot, 0, 1, 0);

        if (itemCount > 0)
        {
            GlStateManager.pushMatrix();
            float scale = 0.25F;
            GlStateManager.scale(scale, scale, scale);
            RenderItem renderItem = mc.getRenderItem();

            GlStateManager.translate((itemCount - 1) / 2F * 0.75, 0.25, 0);

            int renderCount = 0;
            double angle = Math.sin(System.currentTimeMillis() % 4000 / 2000F * Math.PI) * 40;
            for (int i = 0; i < te.inventory.getSlots(); ++i)
            {
                ItemStack stack = te.inventory.getStackInSlot(i);
                if (!stack.isEmpty())
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.rotate((float) angle, 0, 1, 0);
                    renderItem.renderItem(stack, ItemCameraTransforms.TransformType.NONE);
                    GlStateManager.popMatrix();
                    GlStateManager.translate(-0.75, renderCount % 2 == 0 ? -0.5 : 0.5, 0);
                    if (++renderCount > itemCount)
                    {
                        break;
                    }
                }
            }
            GlStateManager.popMatrix();
        }

        if (size > 0)
        {
            GlStateManager.enableBlend();
            GlStateManager.disableCull();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.pushMatrix();
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(CuisineFluids.JUICE.getFlowing().toString());
            if (flowing == null)
            {
                flowing = mc.getTextureMapBlocks().getMissingSprite();
            }

            double height = 0.125 - 0.5 + size / te.builder.getMaxSize() * 0.75;

            int color = te.builder.getColor();
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            int a = color >> 24 & 0xFF;

            if (size < te.builder.getMaxSize())
            {
                TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(CuisineFluids.JUICE.getStill().toString());
                if (still == null)
                {
                    still = mc.getTextureMapBlocks().getMissingSprite();
                }
                buffer.pos(-0.375, height, -0.3125).tex(still.getMinU(), still.getMinV()).color(r, g, b, a).endVertex();
                buffer.pos(-0.375, height, +0.375).tex(still.getMinU(), still.getMaxV()).color(r, g, b, a).endVertex();
                buffer.pos(+0.375, height, +0.375).tex(still.getMaxU(), still.getMaxV()).color(r, g, b, a).endVertex();
                buffer.pos(+0.375, height, -0.3125).tex(still.getMaxU(), still.getMinV()).color(r, g, b, a).endVertex();
            }

            buffer.pos(+0.375, height, -0.3125).tex(flowing.getInterpolatedU(8), flowing.getMinV()).color(r, g, b, a).endVertex();
            buffer.pos(-0.375, height, -0.3125).tex(flowing.getMinU(), flowing.getMinV()).color(r, g, b, a).endVertex();
            buffer.pos(-0.375, 0.125 - 0.5, -0.3125).tex(flowing.getMinU(), flowing.getInterpolatedV(size / te.builder.getMaxSize() * 16)).color(r, g, b, a).endVertex();
            buffer.pos(+0.375, 0.125 - 0.5, -0.3125).tex(flowing.getInterpolatedU(8), flowing.getInterpolatedV(size / te.builder.getMaxSize() * 16)).color(r, g, b, a).endVertex();

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.enableCull();
            RenderHelper.enableStandardItemLighting();
        }

        GlStateManager.popMatrix();
    }
}
