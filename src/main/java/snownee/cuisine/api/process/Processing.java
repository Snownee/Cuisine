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

    // Basin squeezing
    public static final CuisineProcessingRecipeManager<BasinInteracting> SQUEEZING = CuisineProcessingRecipeManager.of(BasinInteracting::descendingCompare);

    // Basin item throwing
    public static final CuisineProcessingRecipeManager<BasinInteracting> BASIN_THROWING = CuisineProcessingRecipeManager.of(BasinInteracting::descendingCompare);

    // Basin boiling
    public static final CuisineProcessingRecipeManager<Boiling> BOILING = CuisineProcessingRecipeManager.of();
}
