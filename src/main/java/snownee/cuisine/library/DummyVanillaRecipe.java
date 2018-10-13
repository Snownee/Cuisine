package snownee.cuisine.library;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * An {@code IRecipe} that always fails, used for "removing" recipe without
 * breaking vanilla advancements.
 */
public final class DummyVanillaRecipe implements IRecipe
{

    private ResourceLocation identifier;

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        return NonNullList.create();
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.create();
    }

    @Override
    public boolean isDynamic()
    {
        return false;
    }

    @Override
    public IRecipe setRegistryName(ResourceLocation name)
    {
        this.identifier = Objects.requireNonNull(name);
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName()
    {
        return this.identifier;
    }

    @Override
    public Class<IRecipe> getRegistryType()
    {
        return IRecipe.class;
    }
}
