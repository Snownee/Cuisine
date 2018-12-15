package snownee.cuisine.items;

import snownee.cuisine.Cuisine;
import snownee.kiwi.item.ItemMod;

@Deprecated
public class ItemManual extends ItemMod
{

    public ItemManual(String name)
    {
        super(name);
        setMaxStackSize(1);
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

}
