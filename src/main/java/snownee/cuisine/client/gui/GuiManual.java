package snownee.cuisine.client.gui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import snownee.cuisine.Cuisine;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.client.AdvancedFontRenderer;
import snownee.kiwi.client.FontUtil;
import snownee.kiwi.client.gui.element.DrawableNineSlice;

public class GuiManual extends GuiScreen
{
    private static final ResourceLocation BOOK_GUI_TEXTURES = new ResourceLocation(Cuisine.MODID, "textures/gui/book.png");

    private static int PAGE_HEIGHT = 200;
    private static int PAGE_WIDTH = /*(int) (PAGE_HEIGHT * 0.75F)*/ 150;
    private static int PAGE_MARGIN = 25;
    public static int SKILL_PANEL_WIDTH = PAGE_WIDTH / 2;

    private final DrawableNineSlice pageGrid;
    private boolean twoPages = false;

    private final String chatRoomURL = I18nUtil.translate("gui.chat_room_link");
    private final String mcmodWikiURL = I18nUtil.translate("gui.mcmod_link");

    public GuiManual()
    {
        pageGrid = new DrawableNineSlice(BOOK_GUI_TEXTURES, 0, 0, 75, 75, 25, 25, 25, 25);
        pageGrid.setHeight(PAGE_HEIGHT);
        pageGrid.setWidth(PAGE_WIDTH);
    }

    @Override
    public void initGui()
    {
        this.fontRenderer = AdvancedFontRenderer.INSTANCE;
        this.buttonList.clear();
        this.addButton(new GuiButton(0, (this.width + PAGE_WIDTH - 80) / 2, (this.height - 20) / 2, 80, 20, I18nUtil.translate("gui.openlink")));
        this.addButton(new GuiButton(1, (this.width + PAGE_WIDTH - 80) / 2, (this.height - 20) / 2 + 40, 80, 20, I18n.format("gui.close"))); // Use vanilla language key

        if (mc.getLanguageManager().getCurrentLanguage().getLanguageCode().startsWith("zh"))
        {
            this.addButton(new GuiButton(2, (this.width + PAGE_WIDTH - 80) / 2, (this.height - 20) / 2 - 40, 80, 20, I18nUtil.translate("gui.openwiki")));
        }
    }

    @Override
    public void onGuiClosed()
    {
        // NO-OP
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        twoPages = true;
        mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
        int i = (this.width - getXSize()) / 2;
        int j = (this.height - getYSize()) / 2;
        pageGrid.draw(mc, i, j);
        if (twoPages)
        {
            pageGrid.draw(mc, i + PAGE_WIDTH, j);
        }

        int originX = i + PAGE_MARGIN;
        int originY = j + PAGE_MARGIN;

        String str = I18nUtil.translateWithEscape("gui.welcome");
        List<String> strs = FontUtil.drawSplitStringOverflow(fontRenderer, str, originX, originY, getClientX(), getClientY(), 0, false);
        if (!strs.isEmpty())
        {
            originX += PAGE_WIDTH;
            FontUtil.drawSplitStringOverflow(fontRenderer, strs, originX, originY, getClientX(), getClientY(), 0, false);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private int getXSize()
    {
        return (twoPages ? PAGE_WIDTH + PAGE_WIDTH : PAGE_WIDTH);
    }

    private int getYSize()
    {
        return PAGE_HEIGHT;
    }

    private int getClientX()
    {
        return PAGE_WIDTH - 2 * PAGE_MARGIN + 5; // TODO
    }

    private int getClientY()
    {
        return PAGE_HEIGHT - 2 * PAGE_MARGIN;
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                GuiConfirmOpenLink guiConfirm = new GuiConfirmOpenLink(this, chatRoomURL, 3, true);
                guiConfirm.disableSecurityWarning();
                mc.displayGuiScreen(guiConfirm);
            }
            else if (button.id == 1)
            {
                mc.displayGuiScreen(null);
            }
            else if (button.id == 2)
            {
                GuiConfirmOpenLink guiConfirm = new GuiConfirmOpenLink(this, mcmodWikiURL, 4, true);
                guiConfirm.disableSecurityWarning();
                mc.displayGuiScreen(guiConfirm);
            }
        }
    }

    @Override
    public void confirmClicked(boolean result, int id)
    {
        if (result && (id == 3 || id == 4))
        {
            try
            {
                java.awt.Desktop.getDesktop().browse(new URI(id == 3 ? chatRoomURL : mcmodWikiURL));
            }
            catch (URISyntaxException wrongURI)
            {
                Cuisine.logger.error("The chat room link '{}' seems to be malformed", chatRoomURL);
                Cuisine.logger.debug("Exception caught: {}", wrongURI);
            }
            catch (Exception e)
            {
                Cuisine.logger.error("Couldn't open link", e);
            }
        }
        mc.displayGuiScreen(null);
    }
}
