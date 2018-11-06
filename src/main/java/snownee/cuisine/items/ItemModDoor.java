package snownee.cuisine.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import snownee.cuisine.Cuisine;
import snownee.cuisine.blocks.BlockModDoor;
import snownee.kiwi.item.IModItem;

public class ItemModDoor extends ItemDoor implements IModItem
{
    private final BlockModDoor block;

    public ItemModDoor(BlockModDoor block)
    {
        super(block);
        this.block = block;
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    public Item cast()
    {
        return this;
    }

    @Override
    public String getName()
    {
        return block.getName();
    }

    @Override
    public void register(String modid)
    {
        setRegistryName(modid, getName());
        setTranslationKey(modid + "." + getName());
    }

}
