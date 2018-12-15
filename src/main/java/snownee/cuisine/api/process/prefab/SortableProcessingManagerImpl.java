package snownee.cuisine.api.process.prefab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import snownee.cuisine.api.process.CuisineProcessingRecipe;
import snownee.cuisine.api.process.CuisineSortableProcessingRecipeManager;

public final class SortableProcessingManagerImpl<R extends CuisineProcessingRecipe>
        implements CuisineSortableProcessingRecipeManager<R>
{

    private final List<R> recipes = new ArrayList<>(16);

    private final Comparator<R> comparator;

    public SortableProcessingManagerImpl(Comparator<R> comparator)
    {
        this.comparator = comparator;
    }

    @Override
    public @Nullable R findRecipe(ResourceLocation locator)
    {
        for (R recipe : this.recipes)
        {
            if (locator.equals(recipe.getIdentifier()))
            {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public @Nonnull Collection<R> findAllPossibleRecipes(Object... inputs)
    {
        List<R> list = new ArrayList<>();
        for (R recipe : this.recipes)
        {
            if (recipe.matches(inputs))
            {
                list.add(recipe);
            }
        }
        list.sort(this.comparator);
        return Collections.unmodifiableList(list);
    }

    @Nonnull
    @Override
    public Comparator<R> getComparator()
    {
        return this.comparator;
    }

    @Nonnull
    @Override
    public Collection<R> preview()
    {
        return Collections.unmodifiableList(this.recipes);
    }

    @Override
    public void add(@Nonnull R recipe)
    {
        this.recipes.add(Objects.requireNonNull(recipe, "Attempt to register a recipe without identifier"));
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
    public boolean remove(ResourceLocation identifier)
    {
        return recipes.removeIf(r -> r.getIdentifier().equals(identifier));
    }

    @Override
    public void removeAll()
    {
        recipes.clear();
    }
}
