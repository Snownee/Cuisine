package snownee.cuisine.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.tiles.TileDrinkroBase;

public class TESRDrinkroBase extends TileEntitySpecialRenderer<TileDrinkroBase>
{
    @Override
    public void render(TileDrinkroBase te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        Minecraft mc = Minecraft.getMinecraft();
        if (!CuisineConfig.GENERAL.alwaysRenderDrinkro && y > mc.player.eyeHeight)
        {
            return;
        }

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

        if (te.inventory == null)
        {
            return;
        }
        ItemStack stack = te.inventory.getStackInSlot(0);
        if (stack.isEmpty())
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

        GlStateManager.rotate(rot, 0, 1, 0);
        float scale = 0.5F;
        GlStateManager.scale(scale, scale, scale);
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

        GlStateManager.translate(0, -0.3, -0.3);
        renderItem.renderItem(stack, ItemCameraTransforms.TransformType.NONE);

        GlStateManager.popMatrix();
    }
}
