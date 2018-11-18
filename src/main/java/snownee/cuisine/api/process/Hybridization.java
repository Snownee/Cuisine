package snownee.cuisine.api.process;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public final class Hybridization implements CuisineProcessingRecipe
{
    private ResourceLocation id;
    private final List<IBlockState> statesA;
    private final List<IBlockState> statesB;
    private final IBlockState resultState;
    private final List<ItemStack> displayItemsA;
    private final List<ItemStack> displayItemsB;
    private final List<ItemStack> displayResultItems;

    public Hybridization(List<IBlockState> statesA, List<IBlockState> statesB, IBlockState resultState, List<ItemStack> displayItemsA, List<ItemStack> displayItemsB, List<ItemStack> displayResultItems)
    {
        this.statesA = statesA;
        this.statesB = statesB;
        this.resultState = resultState;
        this.displayItemsA = displayItemsA;
        this.displayItemsB = displayItemsB;
        this.displayResultItems = displayResultItems;
    }

    @Override
    public boolean matches(Object... inputs)
    {
        return inputs.length == 2 && statesA.contains(inputs[0]) && statesB.contains(inputs[1]);
    }

    public List<ItemStack> getDisplayItemsA()
    {
        return displayItemsA;
    }

    public List<ItemStack> getDisplayItemsB()
    {
        return displayItemsB;
    }

    public List<ItemStack> getDisplayResultItems()
    {
        return displayResultItems;
    }

    public List<IBlockState> getStatesA()
    {
        return statesA;
    }

    public List<IBlockState> getStatesB()
    {
        return statesB;
    }

    public IBlockState getResultState()
    {
        return resultState;
    }

    /**
     * @deprecated use {@link #setIdentifier(ResourceLocation)} instead
     * @param id the identifier, assuming the namespace is <code>cuisine</code>.
     */
    @Deprecated
    public void setID(String id)
    {
        this.id = new ResourceLocation("cuisine", id);
    }

    public void setIdentifier(ResourceLocation locator)
    {
        this.id = locator;
    }

    @Override
    public @Nonnull ResourceLocation getIdentifier()
    {
        return id;
    }

}
