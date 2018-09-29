package snownee.cuisine.client;

import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
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

    // TODO: width measure method? or icons should not wrap line?
}
