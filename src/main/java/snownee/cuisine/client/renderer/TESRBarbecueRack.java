package snownee.cuisine.client.renderer;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.client.CulinaryRenderHelper;
import snownee.cuisine.client.gui.CuisineGUI;
import snownee.cuisine.client.renderer.HoloProfiles.HoloProfile;
import snownee.cuisine.tiles.TileBarbecueRack;
import snownee.kiwi.util.AABBUtil;

public class TESRBarbecueRack extends TileEntitySpecialRenderer<TileBarbecueRack>
{
    @Override
    public void render(TileBarbecueRack tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        Minecraft mc = Minecraft.getMinecraft();
        RenderItem renderItem = mc.getRenderItem();

        EnumFacing facing = EnumFacing.NORTH;
        if (tile.hasWorld())
        {
            facing = CuisineRegistry.FIRE_PIT.getStateFromMeta(tile.getBlockMetadata()).getValue(BlockHorizontal.FACING);
        }

        GlStateManager.pushMatrix();

        if (Minecraft.getMinecraft().gameSettings.showDebugInfo)
        {

            AxisAlignedBB aabbItem = AABBUtil.rotate(new AxisAlignedBB(0.3D, 0.5D, 0.2D, 0.7D, 0.9D, 0.4D), facing);
            AxisAlignedBB aabbEmpty = AABBUtil.rotate(new AxisAlignedBB(0.45D, 0.65D, 0.2D, 0.55D, 0.75D, 0.4D), facing);
            GlStateManager.disableTexture2D();
            for (int i = 0; i < 3; i++)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                RenderGlobal.drawSelectionBoundingBox((tile.stacks.getStackInSlot(2 - i).isEmpty() ? aabbEmpty : aabbItem).offset(facing.getDirectionVec().getX() * 0.2 * i, 0, facing.getOpposite().getDirectionVec().getZ() * 0.2 * i), 0, 0, 0, 1);
                GlStateManager.popMatrix();
            }
            GlStateManager.enableTexture2D();
        }

        GlStateManager.translate(x + 0.5, y + 0.7, z + 0.5);
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.rotate(facing.getHorizontalAngle(), 0, 1, 0.1F);
        GlStateManager.translate(0, 0, -0.4);
        float rotate = 0.15F;
        for (int i = 0; i < 3; ++i)
        {
            ItemStack stack = tile.stacks.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            }
            GlStateManager.translate(0, 0, 0.4);
            rotate *= -1.2;
            GlStateManager.rotate(10, rotate * 1.5F, 0, rotate);
        }
        GlStateManager.popMatrix();

        HoloProfile profile = HoloProfiles.get(tile);
        int heat = (int) (mc.getSystemTime() % 15000 / 5);
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
            long countStacks = tile.stacks.getStacks().stream().filter(e -> !e.isEmpty()).count();
            int width = 20;
            if (countStacks > 0)
            {
                width += 40;
            }
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
}
