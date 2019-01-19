package snownee.cuisine.plugins.patchouli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.food.Drink.DrinkType;
import snownee.kiwi.crafting.input.ProcessingInput;
import snownee.kiwi.util.NBTHelper;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;

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
        drinkType = DrinkType.DRINK_TYPES.get(drink_type);
        if (drinkType != null)
        {
            drink = new ItemStack(CuisineRegistry.DRINK);
            NBTHelper.of(drink).setString("model", drinkType.getName());
            containers = drinkType.getContainerPre().examples();
            featureInputs = new ArrayList<>();
            for (Entry<ProcessingInput, DrinkType> e : Drink.Builder.FEATURE_INPUTS.entrySet())
            {
                if (e.getValue() == drinkType)
                {
                    featureInputs.add(e.getKey().examples());
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
        parent.getFont().drawString(I18n.format(drinkType.getTranslationKey() + ".name"), 0, 0, book.textColor);
    }

}
