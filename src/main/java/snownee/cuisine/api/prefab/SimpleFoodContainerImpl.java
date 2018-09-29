package snownee.cuisine.api.prefab;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.FoodContainer;

/**
 * A demonstrative implementation of {@link FoodContainer}. It always
 * consider the current container as "reusable", and thus return
 * the given {@link ItemStack} itself in {@link #getEmptyContainer}.
 */
public class SimpleFoodContainerImpl implements FoodContainer
{

    protected CompositeFood food = null;

    @Nullable
    @Override
    public CompositeFood get()
    {
        CompositeFood result;
        return (result = this.food) == null || result.isEmpty() ? null : result;
    }

    @Override
    public void set(@Nullable CompositeFood newFoodInstance)
    {
        this.food = newFoodInstance;
    }

    @Nonnull
    @Override
    public ItemStack getEmptyContainer(ItemStack currentContainer)
    {
        return currentContainer;
    }
}
