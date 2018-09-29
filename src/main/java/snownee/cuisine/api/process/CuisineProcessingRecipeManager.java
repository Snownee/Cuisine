package snownee.cuisine.api.process;

import java.util.Collection;
import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import snownee.cuisine.api.process.prefab.ProcessingManagerImpl;
import snownee.cuisine.api.process.prefab.SortableProcessingManagerImpl;

public interface CuisineProcessingRecipeManager<R extends CuisineProcessingRecipe>
{

    /**
     *
     * @return a view-only {@linkplain Collection collection} that contains all
     *         recipe objects that this manager holds.
     */
    @Nonnull
    Collection<R> preview();

    /**
     * Add a new recipe object to this manager object.
     * 
     * @param recipe
     *            The recipe object to add.
     */
    void add(@Nonnull R recipe);

    /**
     * Remove an existent recipe object by its ingredients.
     * 
     * @param inputs The inputs of recipe object to remove.
     *            
     * @return true if recipe has been successfully removed.
     */
    boolean remove(@Nonnull Object... inputs);

    /**
     * Remove an existent recipe object according to equivalence.
     *
     * @param recipe The recipe object to remove
     * @return <code>true</code> if the recipe has been successfully remove.
     */
    boolean remove(R recipe);

    /**
     * Remove all existent recipes
     */
    void removeAll();

    /**
     *
     * @return recipe object that matches inputs; null if otherwise.
     */
    @Nullable
    R findRecipe(Object... inputs);

    /**
    *
    * @return Collection of recipe objects that match inputs, may be empty; null is
    *         prohibited.
    */
    @Nonnull
    Collection<R> findAllPossibleRecipes(Object... inputs);

    static <R extends CuisineProcessingRecipe> CuisineProcessingRecipeManager<R> of()
    {
        return new ProcessingManagerImpl<>();
    }

    static <R extends CuisineProcessingRecipe> CuisineProcessingRecipeManager<R> of(Comparator<R> comparator)
    {
        return new SortableProcessingManagerImpl<>(comparator);
    }

}
