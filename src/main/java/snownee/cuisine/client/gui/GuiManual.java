package snownee.cuisine.client.gui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinarySkill;
import snownee.cuisine.api.CulinarySkillManager;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.client.AdvancedFontRenderer;
import snownee.kiwi.client.FontUtil;
import snownee.kiwi.client.gui.element.DrawableNineSlice;

public class GuiManual extends GuiScreen
{
    private static final ResourceLocation BOOK_GUI_TEXTURES = new ResourceLocation(Cuisine.MODID, "textures/gui/book.png");

    public static int PAGE_HEIGHT = 200;
    public static int PAGE_WIDTH = (int) (PAGE_HEIGHT * 0.75F);
    public static int PAGE_MARGIN = 25;
    public static int SKILL_PANEL_WIDTH = PAGE_WIDTH / 2;

    public final DrawableNineSlice pageGrid;
    private boolean twoPages = false;

    private int currentSkillPage = 1;
    private int maxSkillPages = CulinarySkillManager.getSkills().size() / 4;
    private GuiButton buttonSkillNextPage;
    private GuiButton buttonSkillPreviousPage;

    private GuiButton buttonNextPage;
    private GuiButton buttonPreviousPage;
    private GuiButton buttonDone;

    private final ItemStack stack;

    private final String chatRoomURL = I18nUtil.translate("gui.chat_room_link");

    public GuiManual(int slot, ItemStack stack)
    {
        this.stack = stack;
        pageGrid = new DrawableNineSlice(BOOK_GUI_TEXTURES, 0, 0, 75, 75, 25, 25, 25, 25);
        pageGrid.setHeight(PAGE_HEIGHT);
        pageGrid.setWidth(PAGE_WIDTH);
    }

    @Override
    public void initGui()
    {
        this.fontRenderer = AdvancedFontRenderer.INSTANCE;
        this.buttonList.clear();
        this.buttonNextPage = this.addButton(new GuiButton(0, (this.width + PAGE_WIDTH - 80/* + SKILL_PANEL_WIDTH*/) / 2, (this.height - 20) / 2 - 20, 80, 20, I18nUtil.translate("gui.openlink")));
        this.buttonDone = this.addButton(new GuiButton(1, (this.width + PAGE_WIDTH - 80/* + SKILL_PANEL_WIDTH*/) / 2, (this.height - 20) / 2 + 20, 80, 20, I18n.format("gui.close"))); // Use vanilla language key
        //this.buttonSkillNextPage = this.addButton(new GuiButton(2, (this.width - getXSize()) / 2 - 10, (this.height - getYSize()) / 2 + PAGE_HEIGHT - 10, 10, 10, ">"));
        //this.buttonSkillPreviousPage = this.addButton(new GuiButton(3, (this.width - getXSize()) / 2 - SKILL_PANEL_WIDTH, (this.height - getYSize()) / 2 + PAGE_HEIGHT - 10, 10, 10, "<"));
    }

    @Override
    public void onGuiClosed()
    {
        // NO-OP
    }

    private void drawSkillInfo()
    {
        int u = (this.width - getXSize()) / 2 + 5;
        int v = (this.height - getYSize()) / 2 + 10;

        List<CulinarySkill> skills = new ArrayList<>(CulinarySkillManager.getSkills());
        int s = (currentSkillPage - 1) * 4;
        for (int i = 0; i < 4 && s + i < skills.size(); i++)
        {
            String skill = skills.get(s + i).getName();
            String name = I18n.format(skills.get(s + i).getTranslationKey());
            int level = 123;// TODO get level
            FontUtil.drawSplitStringOverflow(fontRenderer, skill + ": " + 123, u, v + 30 * i, SKILL_PANEL_WIDTH - 20, PAGE_HEIGHT, 0, false);
        }
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
        pageGrid.draw(mc, i/* + SKILL_PANEL_WIDTH*/, j);
        if (twoPages)
        {
            pageGrid.draw(mc, i + PAGE_WIDTH/* + SKILL_PANEL_WIDTH*/, j);
        }

        //mc.getTextureManager().bindTexture(skillPanel);
        //pageGrid.draw(i, j, SKILL_PANEL_WIDTH, PAGE_HEIGHT, false, false);
        //this.drawTexturedModalRect(i, j, 0, 0, SKILL_PANEL_WIDTH, PAGE_HEIGHT); // Same below

        //drawSkillInfo(); // TODO (3TUSK): DO NOT USE UNLESS YOU ARE PART OF CUISINE DEV TEAM

        int originX = i + PAGE_MARGIN/* + SKILL_PANEL_WIDTH*/; // Same above
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
        return (twoPages ? PAGE_WIDTH + PAGE_WIDTH : PAGE_WIDTH);// + SKILL_PANEL_WIDTH;
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
                if (currentSkillPage < maxSkillPages)
                {
                    currentSkillPage++;
                }
            }
            else if (button.id == 3)
            {
                if (currentSkillPage > 1)
                {
                    currentSkillPage--;
                }
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
