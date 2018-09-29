package snownee.cuisine.library;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class FilterItemHandler implements IItemHandler
{
    private final IItemHandler parent;
    private final IntObjectBiPredicate<ItemStack> predicate;

    public FilterItemHandler(IItemHandler parent, IntObjectBiPredicate<ItemStack> predicate)
    {
        this.parent = parent;
        this.predicate = predicate;
    }

    @Override
    public int getSlots()
    {
        return parent.getSlots();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot)
    {
        return parent.getStackInSlot(slot);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        return predicate.test(slot, stack) ? parent.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return parent.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return parent.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return predicate.test(slot, stack);
    }
}
