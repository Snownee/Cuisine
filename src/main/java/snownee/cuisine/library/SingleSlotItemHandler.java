package snownee.cuisine.library;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class SingleSlotItemHandler implements IItemHandler
{

    private ItemStack content = ItemStack.EMPTY;

    /**
     * @return The direct reference of its content.
     */
    public ItemStack getRawContent()
    {
        return content;
    }

    public void setRawContent(ItemStack newContent)
    {
        this.content = newContent;
    }

    @Override
    public int getSlots()
    {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return slot == 0 ? content : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (slot == 0)
        {
            if (this.content.isEmpty())
            {
                if (!simulate)
                {
                    this.content = stack;
                }
                return ItemStack.EMPTY;
            }
            else
            {
                if (this.content.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(this.content, stack))
                {
                    if (this.content.getCount() >= this.content.getMaxStackSize())
                    {
                        return stack; // Don't do anything if it reaches limit
                    }

                    int newCount = this.content.getCount() + stack.getCount();
                    int diff = this.content.getMaxStackSize() - newCount;
                    if (diff >= 0) // newCount does not exceed maxStackSize, so we accept all of them
                    {
                        if (!simulate)
                        {
                            this.content.grow(stack.getCount());
                        }
                        return ItemStack.EMPTY;
                    }
                    else // newCount exceeds maxStackSize, so we accept a portion
                    {
                        ItemStack result;
                        int increment = this.content.getMaxStackSize() - this.content.getCount();
                        if (simulate)
                        {
                            result = stack.copy();
                        }
                        else
                        {
                            result = stack;
                            this.content.grow(increment);
                        }
                        result.shrink(increment); // Shrink the increment added to this.content
                        return result;
                    }
                }
                else
                {
                    return stack;
                }
            }
        }
        else
        {
            return stack;
        }
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (slot == 0)
        {
            if (this.content.isEmpty())
            {
                return ItemStack.EMPTY;
            }
            ItemStack source = simulate ? this.content.copy() : this.content;
            return source.splitStack(amount);
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return slot == 0 ? content.getMaxStackSize() : 0;
    }

    public IItemHandlerModifiable asModifiable()
    {
        return new ModifiableView();
    }

    class ModifiableView implements IItemHandlerModifiable
    {

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack)
        {
            if (slot == 0)
            {
                SingleSlotItemHandler.this.setRawContent(stack);
            }
        }

        @Override
        public int getSlots()
        {
            return SingleSlotItemHandler.this.getSlots();
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot)
        {
            return SingleSlotItemHandler.this.getStackInSlot(slot);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            return SingleSlotItemHandler.this.insertItem(slot, stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return SingleSlotItemHandler.this.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return SingleSlotItemHandler.this.getSlotLimit(slot);
        }
    }
}
