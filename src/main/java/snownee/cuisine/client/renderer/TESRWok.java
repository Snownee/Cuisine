package snownee.cuisine.client.renderer;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import snownee.cuisine.tiles.TileWok;

public class TESRWok extends TileEntitySpecialRenderer<TileWok>
{
    @Override
    public void render(TileWok tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        List<ItemStack> list = tile.getWokContents();

        if (list.isEmpty())
        {
            return;
        }

        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();

        GlStateManager.translate(x + 0.5, y + 0.1, z + 0.5);
        int count = 0;

        for (ItemStack stack : list)
        {
            GlStateManager.pushMatrix();
            int seed = stack.hashCode() + tile.actionCycle * 12450;

            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.translate(((seed % 100) - 50) / 150D, 0.5 + count / 1000D, ((seed % 56) - 28) / 84D);
            GlStateManager.rotate(360 * (seed % 943) / 943F, 0, 1, 0);
            GlStateManager.rotate(90, 1, 0, 0);

            RenderHelper.enableStandardItemLighting();
            renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();

            count++;
        }

        GlStateManager.popMatrix();
    }
}
