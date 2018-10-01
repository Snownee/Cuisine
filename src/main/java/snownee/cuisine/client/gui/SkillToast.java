package snownee.cuisine.client.gui;

import java.util.Set;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinarySkill;
import snownee.cuisine.api.CulinarySkillPoint;
import snownee.cuisine.util.I18nUtil;

@SideOnly(Side.CLIENT)
public class SkillToast implements IToast
{
    private final CulinarySkillPoint skillPoint;
    private final int level;
    private final String skills;
    private final boolean noSkill;

    public SkillToast(CulinarySkillPoint skillPoint, short level, Set<CulinarySkill> skills)
    {
        this.skillPoint = skillPoint;
        this.level = level;
        this.noSkill = skills.isEmpty();
        // Make sure field `skills` is not null
        this.skills = skills.stream().map(CulinarySkill::getTranslationKey).map(I18n::format).collect(Collectors.joining(" "));
    }

    @Override
    public Visibility draw(GuiToast toastGui, long delta)
    {
        toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
        GlStateManager.color(1F, 1F, 1F);
        toastGui.drawTexturedModalRect(0, 0, 0, 32, 160, 32);
        if (delta < 3000)
        {
            toastGui.getMinecraft().fontRenderer.drawString(I18nUtil.translate("toast.skill.upgrade"), 10, 7, -11534256);
            toastGui.getMinecraft().fontRenderer.drawString(I18nUtil.translate("toast.skill.description", I18nUtil.translate("skillpoint." + skillPoint.toString()), level), 10, 18, -16777216);
        }
        if (delta > 2000 && noSkill)
        {
            return IToast.Visibility.HIDE;
        }
        if (delta > 2000 && delta < 3000)
        {
            final int height = 24;
            double rad = Math.sin((delta - 2000) / 2000D * Math.PI) * 152;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.disableTexture2D();
            GlStateManager.disableCull();
            GlStateManager.color(1, 1, 1);

            //            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.SRC_ALPHA);
            bufferbuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION);
            bufferbuilder.pos(4, 4, 0).endVertex();
            //            bufferbuilder.pos(4, 4, 0).color(1, 0, 1, 1).endVertex();

            for (int i = 0; i <= 20; i++)
            {
                double y = Math.sin(i / 40F * Math.PI) * rad;
                if (y < 24)
                {
                    bufferbuilder.pos(4 + Math.cos(i / 40F * Math.PI) * rad, 4 + y, 0).endVertex();
                    //                    bufferbuilder.pos(4 + Math.cos(i / 40F * Math.PI) * rad, 4 + y, 0).color(1, 1, 0, 0.5F).endVertex();
                }
                else
                {
                    bufferbuilder.pos(4 + Math.sqrt(rad * rad - height * height), 4 + height, 0).endVertex();
                    bufferbuilder.pos(4, 4 + height, 0).endVertex();
                    //                    bufferbuilder.pos(4 + Math.sqrt(rad * rad - height * height), 4 + height, 0).color(1, 1, 0, 0.5F).endVertex();
                    //                    bufferbuilder.pos(4, 4 + height, 0).color(1, 1, 1, 1).endVertex();
                    break;
                }
            }

            tessellator.draw();
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
        }
        else if (delta >= 3000)
        {
            Gui.drawRect(4, 4, 156, 28, 0xFFFFFFFF);
        }

        if (delta > 2600 && !noSkill)
        {
            //            float alpha = Math.min((delta - 2600) / 1000F, 1);
            //            System.out.println(alpha);
            //            GlStateManager.color(1, 1, 1, alpha);
            GlStateManager.pushMatrix();
            toastGui.getMinecraft().fontRenderer.drawString(I18nUtil.translate("toast.skill.get"), 30, 7, -11534256);
            toastGui.getMinecraft().fontRenderer.drawString(skills, 30, 18, -16777216);
            RenderHelper.enableGUIStandardItemLighting();
            toastGui.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(CuisineRegistry.KITCHEN_KNIFE), 8, 8);
            GlStateManager.popMatrix();
        }

        return delta >= 5000 ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
    }

}
