package snownee.cuisine.api.process.prefab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import snownee.cuisine.Cuisine;
import snownee.cuisine.api.process.IdentityBasedProcessingRecipe;
import snownee.cuisine.api.process.IdentityBasedProcessingRecipeManager;

public final class IdentityBasedProcessingManagerImpl<R extends IdentityBasedProcessingRecipe> implements IdentityBasedProcessingRecipeManager<R>
{
    private final Map<String, R> recipes = new HashMap<>(16);

    @Override
    public Collection<R> preview()
    {
        return Collections.unmodifiableCollection(recipes.values());
    }

    @Override
    public void add(R recipe)
    {
        String id = recipe.getID();
        if (id == null || id.isEmpty())
        {
            Cuisine.logger.error("Try to add a recipe {} without ID into recipe manager {}, skipping");
        }
        else
        {
            recipes.put(id, recipe);
        }
    }

    @Override
    public boolean remove(Object... inputs)
    {
        boolean flag = false;
        for (Entry<String, R> entry : recipes.entrySet())
        {
            if (entry.getValue().matches(inputs))
            {
                recipes.remove(entry.getKey());
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean remove(R recipe)
    {
        boolean flag = false;
        for (Entry<String, R> entry : recipes.entrySet())
        {
            if (recipe.equals(entry.getValue()))
            {
                recipes.remove(entry.getKey());
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void removeAll()
    {
        recipes.clear();
    }

    @Override
    @Nullable
    public R findRecipe(Object... inputs)
    {
        for (R recipe : recipes.values())
        {
            if (recipe.matches(inputs))
            {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public Collection<R> findAllPossibleRecipes(Object... inputs)
    {
        List<R> list = new ArrayList<>(8);
        for (R recipe : recipes.values())
        {
            if (recipe.matches(inputs))
            {
                list.add(recipe);
            }
        }
        return list;
    }

    @Override
    @Nullable
    public R findRecipeByID(String id)
    {
        return recipes.get(id);
    }

    @Override
    @Nullable
    public R removeRecipeByID(String id)
    {
        return recipes.remove(id);
    }

}
