package snownee.cuisine.library;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.property.IUnlistedProperty;

public final class UnlistedPropertyItemStack implements IUnlistedProperty<ItemStack>
{
    public static UnlistedPropertyItemStack of(final String name)
    {
        return new UnlistedPropertyItemStack(name);
    }

    private String name;

    private UnlistedPropertyItemStack(final String name)
    {

        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isValid(ItemStack value)
    {
        return value != null;
    }

    @Override
    public Class<ItemStack> getType()
    {
        return ItemStack.class;
    }

    @Override
    public String valueToString(ItemStack value)
    {
        return value.toString();
    }
}
