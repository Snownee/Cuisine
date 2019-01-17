package snownee.cuisine.plugins.patchouli;

import java.lang.reflect.Field;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageCenteredText extends PageWithText
{
    String title;

    private static final Field FIELD_TEXT;

    static
    {
        FIELD_TEXT = ReflectionHelper.findField(PageWithText.class, "text");
    }

    @Override
    public int getTextHeight()
    {
        if (pageNum == 0)
            return 22;

        if (title != null && !title.isEmpty())
            return 12;

        return -4;
    }

    @Override
    public void render(int mouseX, int mouseY, float pticks)
    {
        if (shouldRenderText())
        {
            try
            {
                parent.drawCenteredStringNoShadow((String) FIELD_TEXT.get(this), GuiBook.PAGE_WIDTH / 2, GuiBook.TOP_PADDING, 0);
            }
            catch (IllegalArgumentException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        if (pageNum == 0)
        {
            boolean renderedSmol = false;
            String smolText = "";

            if (mc.gameSettings.advancedItemTooltips)
            {
                ResourceLocation res = parent.getEntry().getResource();
                smolText = res.toString();
            }
            else if (entry.isExtension())
            {
                String name = entry.getTrueProvider().getOwnerName();
                smolText = I18n.format("patchouli.gui.lexicon.added_by", name);
            }

            if (!smolText.isEmpty())
            {
                GlStateManager.scale(0.5F, 0.5F, 1F);
                parent.drawCenteredStringNoShadow(smolText, GuiBook.PAGE_WIDTH, 12, book.headerColor);
                GlStateManager.scale(2F, 2F, 1F);
                renderedSmol = true;
            }

            parent.drawCenteredStringNoShadow(parent.getEntry().getName(), GuiBook.PAGE_WIDTH / 2, renderedSmol ? -3 : 0, book.headerColor);
            GuiBook.drawSeparator(book, 0, 12);
        }

        else if (title != null && !title.isEmpty())
            parent.drawCenteredStringNoShadow(title, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
    }

}
