package snownee.cuisine.client;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.client.gui.CuisineGUI;

@SideOnly(Side.CLIENT)
public class CulinaryRenderHelper
{
    public static void renderMaterialCategoryIcon(MaterialCategory category, int x, int y)
    {
        renderMaterialCategoryIcon(category, x, y, 1, 0);
    }

    public static void renderMaterialCategoryIcon(MaterialCategory category, int x, int y, double level, double starvation)
    {
        if (category.ordinal() > 10)
        {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(CuisineGUI.TEXTURE_ICONS);

        int u = 96 + category.ordinal() * 16;

        if (level < 1) // draw background
        {
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, 32, 16, 16, 256, 256);
        }
        if (level > 0) // draw main icon
        {
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, 0, (int) (level * 16), 16, 256, 256);
        }
        if (starvation > 0) // draw outline
        {
            float i = Math.abs((Minecraft.getSystemTime() % 2000) / 1000F - 1);
            if (i != 0)
            {
                if (i != 1)
                {
                    GlStateManager.enableBlend();
                    GlStateManager.color(1, 1, 1, i);
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                }
                Gui.drawModalRectWithCustomSizedTexture(x, y, u, 16, (int) (starvation * 16), 16, 256, 256);
                if (i != 1)
                {
                    GlStateManager.disableBlend();
                    GlStateManager.color(1, 1, 1, 1);
                }
            }
        }
    }

    /**
     * 
     * @param categoryToLevel
     * @param x
     * @param y
     * @param starvationModifier
     * @param maxWidth - 图标可在一行中被显示的最大宽度，如果图标过长将尝试缩短间距
     */
    public static void renderMaterialCategoryIcons(Map<MaterialCategory, Double> categoryToLevel, int x, int y, float starvationModifier, int maxWidth)
    {
        int width = 17;
        int sum = categoryToLevel.values().stream().mapToInt(MathHelper::ceil).sum();
        if (sum == 0)
        {
            return;
        }
        if (sum * width > maxWidth)
        {
            width = maxWidth / sum;
        }

        sum = 0;
        for (Entry<MaterialCategory, Double> entry : categoryToLevel.entrySet())
        {
            double foodLevel = entry.getValue();
            int countIcon = MathHelper.ceil(foodLevel);
            double starvation = foodLevel * starvationModifier;
            for (int j = 0; j < countIcon; ++j)
            {
                renderMaterialCategoryIcon(entry.getKey(), x + sum * width, y, Math.min(1, foodLevel - j), Math.min(1, starvation - j));
                ++sum;
            }
        }
    }

    public static void renderColoredGuiItem(Minecraft mc, ItemStack stack, int color)
    {
        RenderItem renderItem = mc.getRenderItem();

        GlStateManager.pushMatrix();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        IBakedModel bakedmodel = renderItem.getItemModelWithOverrides(stack, mc.player.getEntityWorld(), null);

        GlStateManager.scale(1.0F, 1.0F, 1.0F);

        if (bakedmodel.isGui3d())
        {
            GlStateManager.enableLighting();
        }
        else
        {
            GlStateManager.disableLighting();
        }

        bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);

        if (!stack.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            if (bakedmodel.isBuiltInRenderer())
            {
                float a = (color >> 24 & 255) / 255.0F;
                float r = (color >> 16 & 255) / 255.0F;
                float g = (color >> 8 & 255) / 255.0F;
                float b = (color & 255) / 255.0F;
                GlStateManager.color(r, g, b, a);
                GlStateManager.enableRescaleNormal();
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
            }
            else
            {
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

                ItemColors itemColors = mc.getItemColors();
                for (EnumFacing enumfacing : EnumFacing.values())
                {
                    renderQuads(bufferbuilder, bakedmodel.getQuads(null, enumfacing, 0), color, itemColors, stack);
                }
                renderQuads(bufferbuilder, bakedmodel.getQuads(null, null, 0), color, itemColors, stack);
                tessellator.draw();

                if (stack.hasEffect())
                {
                    renderItem.renderEffect(bakedmodel);
                }
            }

            GlStateManager.popMatrix();
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

        GlStateManager.enableLighting();
    }

    private static void renderQuads(BufferBuilder buffer, List<BakedQuad> quads, int color, ItemColors itemColors, ItemStack stack)
    {
        int i = 0;

        for (int j = quads.size(); i < j; ++i)
        {
            BakedQuad bakedquad = quads.get(i);
            int k;

            if (!stack.isEmpty() && bakedquad.hasTintIndex())
            {
                k = itemColors.colorMultiplier(stack, bakedquad.getTintIndex());
                if (EntityRenderer.anaglyphEnable)
                {
                    k = TextureUtil.anaglyphColor(k);
                }
                float a = (color >> 24 & 255) / 255.0F;
                float r = (color >> 16 & 255) / 255.0F * (k >> 16 & 255) / 255.0F;
                float g = (color >> 8 & 255) / 255.0F * (k >> 8 & 255) / 255.0F;
                float b = (color & 255) / 255.0F * (k & 255) / 255.0F;
                k = (int) (a * 255) << 24 | (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255);
            }
            else
            {
                k = color;
            }

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(buffer, bakedquad, k);
        }
    }

    public static void drawModalRect(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight)
    {
        drawModalRect(x, y, u, v, width, height, textureWidth, textureHeight, 0);
    }

    public static void drawModalRect(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight, float z)
    {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, z).tex(u * f, (v + height) * f1).endVertex();
        bufferbuilder.pos(x + width, y + height, z).tex((u + width) * f, (v + height) * f1).endVertex();
        bufferbuilder.pos(x + width, y, z).tex((u + width) * f, v * f1).endVertex();
        bufferbuilder.pos(x, y, z).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }
}
