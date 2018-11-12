package snownee.cuisine.api.process;

import javax.annotation.Nullable;

public interface IdentityBasedProcessingRecipeManager<R extends IdentityBasedProcessingRecipe> extends CuisineProcessingRecipeManager<R>
{
    @Nullable
    R findRecipeByID(String id);

    @Nullable
    R removeRecipeByID(String id);
}
