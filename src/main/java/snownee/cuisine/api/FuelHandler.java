package snownee.cuisine.api;

public interface FuelHandler
{
    void update(float bonusRate);

    float getBurnTime();

    void setBurnTime(float burnTime);

    float getMaxBurnTime();

    void addBurnTime(float delta);
}
