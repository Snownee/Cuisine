package snownee.cuisine.api.process;

public interface IdentityBasedProcessingRecipe extends CuisineProcessingRecipe
{
    void setID(String id);

    String getID();
}
