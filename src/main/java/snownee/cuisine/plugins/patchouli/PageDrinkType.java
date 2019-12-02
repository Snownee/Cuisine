package snownee.cuisine.plugins.patchouli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.food.Drink.DrinkType;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.crafting.input.ProcessingInput;
import snownee.kiwi.util.NBTHelper;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;

public class PageDrinkType extends BookPage
{
    String drink_type;

    private transient DrinkType drinkType;
    private transient ItemStack drink;
    private transient List<ItemStack> containers;
    private transient List<List<ItemStack>> featureInputs;

    @Override
    public void build(BookEntry entry, int pageNum)
    {
        super.build(entry, pageNum);
        drinkType = DrinkType.get(drink_type);
        if (drinkType != null)
        {
            drink = new ItemStack(CuisineRegistry.DRINK);
            NBTHelper.of(drink).setString("model", drinkType.getName()).setInt("liquidColor", 0xF08A19);
            containers = drinkType.getContainerPre().examples();
            featureInputs = new ArrayList<>();
            for (Entry<ProcessingInput, DrinkType> e : Drink.Builder.FEATURE_INPUTS.entrySet())
            {
                if (e.getValue() == drinkType)
                {
                    List<ItemStack> examples = e.getKey().examples();
                    if (!examples.isEmpty())
                    {
                        featureInputs.add(examples);
                    }
                }
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float pticks)
    {
        if (drinkType == null)
        {
            return;
        }
        parent.drawCenteredStringNoShadow(I18n.format(drinkType.getTranslationKey() + ".name"), GuiBook.PAGE_WIDTH / 2, -4, book.textColor);

        int w = 68;
        int h = 26;
        mc.renderEngine.bindTexture(book.craftingResource);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(GuiBook.PAGE_WIDTH / 2 - w / 2, 10, 0, 128 - h, w, h, 128, 128);

        parent.renderItemStack(GuiBook.PAGE_WIDTH / 2 - 9, 15, mouseX, mouseY, drink);

        if (drinkType != DrinkType.NORMAL)
        {
            parent.getFont().drawString(I18nUtil.translate("manual.featureInput"), 4, 40, book.textColor);
        }
        parent.getFont().drawString(I18nUtil.translate("manual.container"), 4, drinkType == DrinkType.NORMAL ? 40 : 80, book.textColor);
        parent.getFont().drawString(I18nUtil.translate("manual.potionVege", I18n.format(drinkType.getPotionVege().getName())), 4, 120, book.textColor);
        parent.getFont().drawString(I18nUtil.translate("manual.potionFruit", I18n.format(drinkType.getPotionFruit().getName())), 4, 130, book.textColor);

        int x = 0;
        for (List<ItemStack> inputs : featureInputs)
        {
            drawItems(x, 48, mouseX, mouseY, inputs);
            x += 24;
        }
        drawItems(0, drinkType == DrinkType.NORMAL ? 48 : 88, mouseX, mouseY, containers);
    }

    private void drawItems(int x, int y, int mouseX, int mouseY, List<ItemStack> items)
    {
        if (items.isEmpty())
        {
            return;
        }
        drawItem(x, y, mouseX, mouseY, items.get(items.size() == 1 ? 0 : (int) (Minecraft.getSystemTime() / 1000 % items.size())));
    }

    private void drawItem(int x, int y, int mouseX, int mouseY, ItemStack item)
    {
        int size = 26;

        mc.renderEngine.bindTexture(book.craftingResource);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x, y, 20, 128 - size, size, size, 128, 128);

        parent.renderItemStack(x + 5, y + 5, mouseX, mouseY, item);
    }

}
