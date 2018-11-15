package snownee.cuisine.api.process;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public abstract class AbstractCuisineProcessingRecipe implements CuisineProcessingRecipe
{

    private final ResourceLocation identifier;

    protected AbstractCuisineProcessingRecipe(ResourceLocation identifier)
    {
        this.identifier = identifier;
    }

    @Override
    public @Nonnull ResourceLocation getIdentifier()
    {
        return this.identifier;
    }
}
