package snownee.cuisine.plugins.patchouli;

import org.apache.logging.log4j.Level;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import snownee.cuisine.Cuisine;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;

public class PageCenteredText extends BookPage
{
    String title;
    String text;

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
        try
        {
            parent.drawCenteredStringNoShadow(text, GuiBook.PAGE_WIDTH / 2, getTextHeight(), 0);
        }
        catch (Exception e)
        {
            Cuisine.logger.catching(Level.DEBUG, e);
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
