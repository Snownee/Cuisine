package snownee.cuisine.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import snownee.cuisine.client.CulinaryRenderHelper;
import snownee.cuisine.client.gui.CuisineGUI;
import snownee.cuisine.client.renderer.HoloProfiles.HoloProfile;
import snownee.cuisine.tiles.TileFirePit;

public abstract class TESRFirePit<T extends TileFirePit> extends TileEntitySpecialRenderer<T>
{
    @Override
    public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        Minecraft mc = Minecraft.getMinecraft();
        RenderItem renderItem = mc.getRenderItem();

        HoloProfile profile = HoloProfiles.get(tile);
        // int heat = (int) (mc.getSystemTime() % 15000 / 5);
        int heat = (int) tile.heatHandler.getHeat();
        boolean focusing = mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.getBlockPos().equals(tile.getPos());
        float transparency = profile.update(focusing, heat, partialTicks);
        if (transparency > 0)
        {
            //GlStateManager.enableAlpha();
            //GlStateManager.alphaFunc(516, 0.1F);
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
            int width = 20 + getWidth(tile);
            int offsetX;
            if (mc.player.getPrimaryHand() == EnumHandSide.RIGHT)
            {
                offsetX = -42 - width;
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
                GlStateManager.color(1, 1, 1, 1 + profile.icon0 / 0.1f);
            }
            CulinaryRenderHelper.drawModalRect(-4, -10 + 80 * (1 - profile.icon0), 96, 48, 16, 16, 256, 256, .1f);
            GlStateManager.color(1, 1, 1, transparency);
            if (profile.icon1 < 0)
            {
                GlStateManager.color(1, 1, 1, 1 + profile.icon1 / 0.1f);
            }
            CulinaryRenderHelper.drawModalRect(-4, -10 + 80 * (1 - profile.icon1), 112, 48, 16, 16, 256, 256, .2f);
            GlStateManager.color(1, 1, 1, transparency);
            CulinaryRenderHelper.drawModalRect(-4, -10 + 80 * (1 - profile.icon2), 128, 48, 16, 16, 256, 256, .3f);

            GlStateManager.popMatrix();

            // 渲染测试用物品（2D扁平）
            //            GlStateManager.rotate(180, 0, 1, 0);
            //            int color = (int) (transparency * 255) << 24 | 0xFFFFFF;
            //            CulinaryRenderHelper.renderColoredGuiItem(mc, new ItemStack(Blocks.TALLGRASS, 1, 1), color);

            GlStateManager.popMatrix();
        }
    }

    protected abstract int getWidth(T tile);
}
