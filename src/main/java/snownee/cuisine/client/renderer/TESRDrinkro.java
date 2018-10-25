package snownee.cuisine.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import snownee.cuisine.tiles.TileDrinkro;

public class TESRDrinkro extends TileEntitySpecialRenderer<TileDrinkro>
{
    @Override
    public void render(TileDrinkro te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

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
        if (itemCount == 0)
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

        int meta = 0;
        if (te.hasWorld())
        {
            meta = te.getBlockMetadata() & 3;
        }
        int rot = 0;
        if (meta == 1)
        {
            rot = 90;
        }
        else if (meta == 0)
        {
            rot = 180;
        }
        else if (meta == 3)
        {
            rot = -90;
        }

        GlStateManager.rotate(rot, 0, 1, 0);
        float scale = te.isBase ? 0.5F : 0.25F;
        GlStateManager.scale(scale, scale, scale);
        Minecraft mc = Minecraft.getMinecraft();
        RenderItem renderItem = mc.getRenderItem();

        if (te.isBase)
        {
            GlStateManager.translate(0, -0.25, 0);
            ItemStack stack = te.inventory.getStackInSlot(0);
            renderItem.renderItem(stack, TransformType.NONE);
        }
        else
        {
            GlStateManager.translate((itemCount - 1) / 2F, 0.25, 0);

            int renderCount = 0;
            double angle = Math.sin(System.currentTimeMillis() % 4000 / 2000F * Math.PI) * 40;
            for (int i = 0; i < te.inventory.getSlots(); ++i)
            {
                ItemStack stack = te.inventory.getStackInSlot(i);
                if (!stack.isEmpty())
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.rotate((float) angle, 0, 1, 0);
                    renderItem.renderItem(stack, TransformType.NONE);
                    GlStateManager.popMatrix();
                    GlStateManager.translate(-1, renderCount % 2 == 0 ? -0.5 : 0.5, 0);
                    if (++renderCount > itemCount)
                    {
                        break;
                    }
                }
            }
        }

        GlStateManager.popMatrix();
    }
}
