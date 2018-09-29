package snownee.cuisine.api.process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CuisineSortableProcessingRecipeManager<R extends CuisineProcessingRecipe>
        extends CuisineProcessingRecipeManager<R>
{

    /**
     * {@inheritDoc}
     *
     * @implSpec Default implementation for CuisineSortableProcessingRecipeManager is
     *           priority-based. It first find all matched R objects, and then sort
     *           it based on comparator provided, and return the object at index 0,
     *           which has the highest priority. The exact definition of priority is
     *           left for comparator.
     *
     * @return recipe object that matches inputs; null if otherwise.
     */
    @Override
    @Nullable
    default R findRecipe(Object... inputs)
    {
        List<R> recipes = new ArrayList<>(this.findAllPossibleRecipes(inputs));
        if (recipes.isEmpty())
        {
            return null;
        }
        else
        {
            recipes.sort(this.getComparator());
            return recipes.get(0);
        }
    }

    /**
     * The getter of comparator used to sort internally stored objects.
     * 
     * @return The comparator instance, may not be null.
     */
    @Nonnull
    Comparator<R> getComparator();
}
