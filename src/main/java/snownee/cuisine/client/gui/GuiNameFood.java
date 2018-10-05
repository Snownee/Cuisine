package snownee.cuisine.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.inventory.ContainerNameFood;
import snownee.cuisine.network.PacketNameFood;
import snownee.cuisine.tiles.TileWok;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.network.NetworkChannel;

@SideOnly(Side.CLIENT)
public class GuiNameFood extends GuiContainer
{
    /** "Done" button for the GUI. */
    private GuiButton doneBtn;
    private GuiTextField textField;
    private boolean synced = false;

    public GuiNameFood(TileWok tile)
    {
        super(new ContainerNameFood(tile));
    }

    @Override
    public void initGui()
    {
        this.xSize = width;
        this.ySize = height;
        super.initGui();
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.doneBtn = this.addButton(new GuiButton(0, this.width / 2 - 60, this.height / 4 + 120, 120, 20, I18n.format("gui.done")));
        this.textField = new GuiTextField(2, this.fontRenderer, this.width / 2 - 100, this.height / 4 + 90, 200, 20);
        this.textField.setMaxStringLength(50);
        this.textField.setFocused(true);
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        this.textField.updateCursorCounter();
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                if (!textField.getText().isEmpty() && !textField.getText().equals(inventorySlots.getSlot(0).getStack().getDisplayName()))
                {
                    NetworkChannel.INSTANCE.sendToServer(new PacketNameFood(textField.getText()));
                }
                else
                {
                    this.mc.displayGuiScreen(null);
                    this.mc.player.closeScreen();
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        this.textField.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_RETURN)
        {
            this.actionPerformed(this.doneBtn);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18nUtil.translate("gui.name_food"), this.width / 2, 40, 16777215);

        this.textField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);

        ItemStack stack = inventorySlots.getSlot(0).getStack();
        if (!stack.isEmpty())
        {
            if (!synced)
            {
                textField.setText(stack.getDisplayName());
                textField.setCursorPositionEnd();
                textField.setSelectionPos(0);
                synced = true;
            }
            GlStateManager.translate(this.width / 2F, this.height / 2F, 32F);
            GlStateManager.pushMatrix();
            GlStateManager.scale(3, 3, 0);
            this.zLevel = -1F;
            this.itemRender.zLevel = -1F;
            this.itemRender.renderItemAndEffectIntoGUI(stack, -8, -18);
            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            GlStateManager.popMatrix();
            GlStateManager.translate(-this.width / 2F, -this.height / 2F, 0);

            if (mouseX > this.width / 2 - 24 && mouseX < this.width / 2 + 24 && mouseY > this.height / 2 - 54 && mouseY < this.height / 2 - 6)
            {
                this.renderToolTip(stack, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
    }
}
