package snownee.cuisine.api.process.prefab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import snownee.cuisine.api.process.CuisineProcessingRecipe;
import snownee.cuisine.api.process.CuisineProcessingRecipeManager;

public final class ProcessingManagerImpl<R extends CuisineProcessingRecipe> implements CuisineProcessingRecipeManager<R>
{

    private final List<R> recipes = new ArrayList<>(16);

    @Nonnull
    @Override
    public List<R> preview()
    {
        return Collections.unmodifiableList(recipes);
    }

    @Override
    public void add(@Nonnull R recipe)
    {
        this.recipes.add(recipe);
    }

    @Override
    public boolean remove(Object... inputs)
    {
        return recipes.removeIf(recipe -> recipe.matches(inputs));
    }

    @Override
    public boolean remove(R recipe)
    {
        return recipes.removeIf(recipe::equals);
    }

    @Override
    public void removeAll()
    {
        recipes.clear();
    }

    @Nullable
    @Override
    public R findRecipe(Object... inputs)
    {
        for (R r : recipes)
        {
            if (r.matches(inputs))
            {
                return r;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Collection<R> findAllPossibleRecipes(Object... inputs)
    {
        List<R> list = new ArrayList<>();
        for (R recipe : this.recipes)
        {
            if (recipe.matches(inputs))
            {
                list.add(recipe);
            }
        }
        return Collections.unmodifiableList(list);
    }
}
