package snownee.cuisine.client.renderer;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import snownee.cuisine.client.CulinaryRenderHelper;
import snownee.cuisine.client.gui.CuisineGUI;
import snownee.cuisine.client.renderer.HoloProfiles.HoloProfile;
import snownee.cuisine.tiles.IHeatable;

public abstract class TESRFirePit<T extends TileEntity & IHeatable> extends TileEntitySpecialRenderer<T>
{
    @Override
    public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        Minecraft mc = Minecraft.getMinecraft();

        HoloProfile profile = HoloProfiles.get(tile);
        // int heat = (int) (mc.getSystemTime() % 15000 / 5);
        int heat = (int) tile.getHeatHandler().getHeat();
        boolean focusing = mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.getBlockPos().equals(tile.getPos());
        float transparency = profile.update(focusing, heat, partialTicks);
        if (transparency > 0)
        {
            //GlStateManager.enableAlpha();
            //GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            if (transparency < 1)
            {
                GlStateManager.color(1, 1, 1, transparency);
            }

            GlStateManager.pushMatrix();
            // 整体位移旋转
            double dx = x + 0.5;
            double dy = y + 0.5;
            double dz = z + 0.5;
            GlStateManager.translate(dx, dy, dz);

            float yaw = (float) (MathHelper.atan2(dx, dz) * (180D / Math.PI));
            GlStateManager.rotate(yaw, 0, 1, 0);
            double distance = MathHelper.sqrt(dx * dx + dz * dz);
            float pitch = (float) (MathHelper.atan2(y - 1, distance) * (-180D / Math.PI));
            GlStateManager.rotate(pitch, 1, 0, 0);

            GlStateManager.disableLighting();

            // 确定界面位置
            List<IngredientInfo> infos = getIngredientInfo(tile);
            int width = 20;
            if (!infos.isEmpty())
            {
                width += ((infos.size() - 1) / 3 + 1) * 20;
            }
            float offsetX;
            if (mc.player.getPrimaryHand() == EnumHandSide.RIGHT)
            {
                profile.extraWidth = HoloProfile.chase(profile.extraWidth, width, partialTicks / 10 * Math.abs(profile.extraWidth - width));
                offsetX = -42 - profile.extraWidth;
            }
            else
            {
                offsetX = 42;
            }

            // 渲染总温度
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180, 0, 0, 0);
            double scale = 1 / 84d;
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(offsetX, -42, 0);
            mc.getTextureManager().bindTexture(CuisineGUI.TEXTURE_ICONS);
            float height = ((heat - profile.minProgress) / (profile.maxProgress - profile.minProgress)) * (80 - 2) + 1;
            CulinaryRenderHelper.drawModalRect(0, 0, 0, 0, 8, 80 - height, 256, 256);
            CulinaryRenderHelper.drawModalRect(0, 80 - height, 8, 80 - height, 8, height, 256, 256);

            if (profile.icon0 < 0)
            {
                GlStateManager.color(1, 1, 1, (1 + profile.icon0 / 0.1f) * transparency);
            }
            CulinaryRenderHelper.drawModalRect(-4, -10 + 80 * (1 - profile.icon0), 96, 48, 16, 16, 256, 256, .1f);
            GlStateManager.color(1, 1, 1, transparency);
            if (profile.icon1 < 0)
            {
                GlStateManager.color(1, 1, 1, (1 + profile.icon1 / 0.1f) * transparency);
            }
            CulinaryRenderHelper.drawModalRect(-4, -10 + 80 * (1 - profile.icon1), 112, 48, 16, 16, 256, 256, .2f);
            GlStateManager.color(1, 1, 1, transparency);
            CulinaryRenderHelper.drawModalRect(-4, -10 + 80 * (1 - profile.icon2), 128, 48, 16, 16, 256, 256, .3f);
            GlStateManager.color(1, 1, 1, 1);

            GlStateManager.popMatrix();

            // 渲染测试用物品（2D扁平）
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.rotate(180, 0, 1, 0);
            scale = 1 / 6d;
            GlStateManager.scale(scale, scale, scale);
            scale = 6 / 84d;
            GlStateManager.translate((offsetX + 20) * scale, 42 * scale, 0);
            int yList = 0;
            int xList = 0;
            for (IngredientInfo info : infos)
            {
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                if (info.doneness <= 100)
                {
                    GlStateManager.color(1, 1, 1, 0.6F * transparency);
                }
                else if (info.doneness < 150)
                {
                    float f = 1 - (info.doneness - 110) / 50F;
                    GlStateManager.color(1, f, f, 0.6F * transparency);
                }
                else
                {
                    GlStateManager.color(1, 0.2F, 0.2F, 0.6F * transparency);
                }
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                buffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION);
                double rad = 0.65;
                if (info.doneness < 100)
                {
                    buffer.pos(xList * 1.5F, -1.5F + yList * 1.5F, 0).endVertex();
                }
                for (float i = Math.min(1, info.doneness / 100F); i > 0; i -= 0.04F)
                {
                    buffer.pos(xList * 1.5F + Math.sin(i / 0.5F * Math.PI) * rad, -1.5F + yList * 1.5F + Math.cos(i / 0.5F * Math.PI) * rad, 0).endVertex();
                }
                buffer.pos(xList * 1.5F, -1.5F + yList * 1.5F + rad, 0).endVertex();
                if (info.doneness < 100)
                {
                    buffer.pos(xList * 1.5F, -1.5F + yList * 1.5F, 0).endVertex();
                }
                tessellator.draw();
                GlStateManager.enableTexture2D();

                CulinaryRenderHelper.renderColoredItem(mc, info.stack, TransformType.GUI, (int) (transparency * 255) << 24 | 0x00FFFFFF, xList * 1.5F, -1.5F + yList * 1.5F);
                // GlStateManager.pushMatrix();
                // GlStateManager.rotate(180, 1, 0, 0);
                // GlStateManager.scale(scale, scale, scale);
                // getFontRenderer().drawString("测试物品", 8, -2 - yList * 11, 0xFF000000);
                // GlStateManager.popMatrix();
                yList -= 1;
                if (yList < -2)
                {
                    yList = 0;
                    ++xList;
                }
            }
            GlStateManager.disableAlpha();
            GlStateManager.popMatrix();

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }

    protected abstract List<IngredientInfo> getIngredientInfo(T tile);

    protected static class IngredientInfo
    {
        ItemStack stack;
        int doneness;

        public IngredientInfo(ItemStack stack, int doneness)
        {
            this.stack = stack;
            this.doneness = doneness;
        }
    }
}
