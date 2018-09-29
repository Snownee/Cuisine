package snownee.cuisine.api.process;

public final class Processing
{

    private Processing()
    {
        throw new UnsupportedOperationException("No instance for you");
    }

    // Mortar grinding
    public static final CuisineProcessingRecipeManager<Grinding> GRINDING = CuisineProcessingRecipeManager.of(Grinding::descendingCompare);

    // Mill grinding
    public static final CuisineProcessingRecipeManager<Milling> MILLING = CuisineProcessingRecipeManager.of();

    // Jarring
    public static final CuisineProcessingRecipeManager<Vessel> VESSEL = CuisineProcessingRecipeManager.of();

    // Chopping board chopping
    public static final CuisineProcessingRecipeManager<Chopping> CHOPPING = CuisineProcessingRecipeManager.of(Chopping::descendingCompare);
}
