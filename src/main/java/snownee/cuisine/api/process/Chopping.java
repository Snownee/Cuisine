package snownee.cuisine.api.process;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import snownee.kiwi.crafting.input.ProcessingInput;

public class Chopping implements CuisineProcessingRecipe
{
    public final ProcessingInput input;
    private final ItemStack output;

    public Chopping(ProcessingInput input, ItemStack output)
    {
        this.input = input;
        this.output = output;
        if (input.isEmpty())
        {
            throw new IllegalArgumentException("Trying to add an invalid chopping recipe with input: " + input);
        }
        if (output.isEmpty())
        {
            throw new IllegalArgumentException("Trying to add an invalid chopping recipe with output: " + output);
        }
    }

    public static int descendingCompare(Chopping a, Chopping b)
    {
        return /*a.input.compareTo(b.input);*/0; // TODO (3TUSK): Fix me
    }

    @Override
    public boolean matches(Object... inputs)
    {
        if (inputs == null || inputs.length != 1)
        {
            return false;
        }
        if (inputs[0] instanceof ItemStack)
        {
            return input.matches((ItemStack) inputs[0]);
        }
        else if (inputs[0].getClass() == Item.class)
        {
            return input.matches(new ItemStack((Item) inputs[0]));
        }
        return false;
    }

    public void consume(IItemHandler inv)
    {
        int invSize = inv.getSlots();
        for (int i = 0; i < invSize; i++)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (input.matches(stack))
            {
                stack = inv.extractItem(i, 1, false);
                if (!stack.isEmpty())
                {
                    inv.insertItem(i, stack.getItem().getContainerItem(stack), false);
                }
                break;
            }
        }
    }

    public ItemStack getOutput()
    {
        return output;
    }

}
