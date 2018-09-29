package snownee.cuisine.items;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import snownee.kiwi.item.IModItem;

public class ItemChoppingBoard extends ItemBlock implements IModItem
{
    private final String name;

    public ItemChoppingBoard(String name, Block block)
    {
        super(block);
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void register(String modid)
    {
        setRegistryName(modid, getName());
        setTranslationKey(modid + "." + getName());
    }

    @Override
    public Item cast()
    {
        return this;
    }

}
