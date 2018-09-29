package snownee.cuisine.plugins.nutrition;

import ca.wescook.nutrition.capabilities.INutrientManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import snownee.cuisine.Cuisine;

public abstract class NutritionCompatibilityPreLoader
{

    private NutritionCompatibilityPreLoader()
    {
        // No-op
    }

    @CapabilityInject(INutrientManager.class)
    private static void injectNutritionCapablity(Capability<INutrientManager> capability)
    {
        try
        {
            NutritionCompat.injectCap(capability);
        }
        catch (Exception e)
        {
            Cuisine.logger.error("Failed to load Nutrition compatibility.", e);
        }
    }
}
