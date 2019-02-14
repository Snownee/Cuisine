package snownee.cuisine.api;

public interface IHeatHandler
{
    void update(float bonusRate);

    float getHeat();

    void setHeat(float heat);

    float getMinHeat();

    float getMaxHeat();

    void addHeat(float delta);

    float getHeatPower();

    float getMaxHeatPower();
}
