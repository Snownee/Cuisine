package snownee.cuisine.api.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import snownee.kiwi.crafting.input.ProcessingInput;

public final class Grinding implements CuisineProcessingRecipe
{

    private final ImmutableList<ProcessingInput> ingredients;
    private final ItemStack output;
    private final int step, totalCount;

    public Grinding(ImmutableList<ProcessingInput> inputs, ItemStack output, int step)
    {
        this.ingredients = inputs;
        this.output = output;
        this.step = step;
        this.totalCount = inputs.stream().mapToInt(ProcessingInput::count).sum();
    }

    @Override
    public boolean matches(Object... inputs)
    {
        List<ItemStack> actualInputs = new ArrayList<>();
        for (Object o : inputs)
        {
            if (o instanceof ItemStack && !((ItemStack) o).isEmpty())
            {
                actualInputs.add((ItemStack) o);
            }
        }
        List<ProcessingInput> recipeInputs = new ArrayList<>(this.ingredients);
        Iterator<ProcessingInput> itr = recipeInputs.iterator();
        while (itr.hasNext())
        {
            ProcessingInput recipeInput = itr.next();
            for (ItemStack actualInput : actualInputs)
            {
                if (recipeInput.matches(actualInput))
                {
                    itr.remove();
                    break;
                }
            }
        }
        return recipeInputs.isEmpty();
    }

    public ImmutableList<ProcessingInput> getInputs()
    {
        return ingredients;
    }

    public ItemStack getOutput()
    {
        return output;
    }

    public int getStep()
    {
        return step;
    }

    public static int descendingCompare(Grinding a, Grinding b)
    {
        return Integer.compare(b.totalCount, a.totalCount); // reversed implementation, to get descending order
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Grinding grinding = (Grinding) o;

        return ingredients.equals(grinding.ingredients);
    }

    @Override
    public int hashCode()
    {
        return ingredients.hashCode();
    }

    public void consume(IItemHandler inv)
    {
        List<ProcessingInput> stacks = new ArrayList<>(this.ingredients);
        int invSize = inv.getSlots();
        for (ProcessingInput ingredient : stacks)
        {
            for (int i = 0; i < invSize; i++)
            {
                if (ingredient.matches(inv.getStackInSlot(i)))
                {
                    inv.extractItem(i, ingredient.count(), false);
                    break;
                }
            }
        }
    }

}
