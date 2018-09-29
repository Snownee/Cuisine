package snownee.cuisine.api;

public interface CookingVessel
{

    /**
     * @return Current temperature of this CookingVessel, in Celsius
     */
    int getTemperature();

    /**
     * @return Current amount of water in this CookingVessel, in milli-bucket (mB)
     */
    int getWaterAmount();

    /**
     * @return Current amount of oil in this CookingVessel, in milli-bucket (mB)
     */
    int getOilAmount();

}
