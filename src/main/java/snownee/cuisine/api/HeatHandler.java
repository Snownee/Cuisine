package snownee.cuisine.api;

public interface HeatHandler
{
    void update(float bonusRate);

    float getHeat();

    void setHeat(float heat);

    float getMaxHeat();

    void addHeat(float delta);
}
