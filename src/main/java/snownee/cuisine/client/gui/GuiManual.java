package snownee.cuisine.client.gui;

import java.net.URI;
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
import snownee.kiwi.client.Modal9Grid;

public class GuiManual extends GuiScreen
{
    private static final ResourceLocation BOOK_GUI_TEXTURES = new ResourceLocation(Cuisine.MODID, "textures/gui/book.png");

    public static int PAGE_HEIGHT = 200;
    public static int PAGE_WIDTH = (int) (PAGE_HEIGHT * 0.75F);
    public static int PAGE_MARGIN = 25;

    public final Modal9Grid pageGrid;
    private boolean twoPages = false;

    private GuiButton buttonNextPage;
    private GuiButton buttonPreviousPage;
    private GuiButton buttonDone;

    private final ItemStack stack;

    private final String chatRoomURL = I18nUtil.translate("gui.chat_room_link");

    public GuiManual(int slot, ItemStack stack)
    {
        this.stack = stack;
        pageGrid = new Modal9Grid(0, 0, 25, 25, 50, 50, 75, 75);
    }

    @Override
    public void initGui()
    {
        this.fontRenderer = AdvancedFontRenderer.INSTANCE;
        this.buttonList.clear();
        this.buttonNextPage = this.addButton(new GuiButton(0, (this.width + PAGE_WIDTH - 80) / 2, (this.height - 20) / 2 - 20, 80, 20, I18nUtil.translate("gui.openlink")));
        this.buttonDone = this.addButton(new GuiButton(1, (this.width + PAGE_WIDTH - 80) / 2, (this.height - 20) / 2 + 20, 80, 20, I18n.format("gui.close"))); // Use vanilla language key
    }

    @Override
    public void onGuiClosed()
    {
        // NO-OP
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        //        PAGE_WIDTH = mouseX + PAGE_MARGIN * 2;
        //        PAGE_HEIGHT = mouseY + PAGE_MARGIN * 2;

        twoPages = true;
        mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
        int i = (this.width - getXSize()) / 2;
        int j = (this.height - getYSize()) / 2;
        pageGrid.draw(i, j, PAGE_WIDTH, PAGE_HEIGHT, false, false);
        if (twoPages)
        {
            pageGrid.draw(i + PAGE_WIDTH, j, PAGE_WIDTH, PAGE_HEIGHT, true, false);
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
        return twoPages ? PAGE_WIDTH + PAGE_WIDTH : PAGE_WIDTH;
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
        }
    }

    @Override
    public void confirmClicked(boolean result, int id)
    {
        if (result && id == 3)
        {
            try
            {
                java.awt.Desktop.getDesktop().browse(new URI(chatRoomURL));
            }
            catch (java.net.URISyntaxException wrongURI)
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
