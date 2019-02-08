package snownee.cuisine.tiles.heat;

import net.minecraft.util.math.MathHelper;
import snownee.cuisine.api.IHeatHandler;

public class HeatHandler implements IHeatHandler
{
    protected float heat, minHeat, maxHeat, heatPower, radiation;

    public HeatHandler()
    {
        heat = 0;
        minHeat = 0;
        maxHeat = 0;
        heatPower = 0;
        radiation = 0;
    }

    public HeatHandler(float minHeat, float maxHeat, float heatPower, float radiation)
    {
        this.minHeat = minHeat;
        this.maxHeat = maxHeat;
        this.heatPower = heatPower;
        this.radiation = radiation;
    }

    @Override
    public void addHeat(float delta)
    {
        heat = MathHelper.clamp(heat + delta, getMinHeat(), getMaxHeat());
    }

    @Override
    public float getMaxHeatPower()
    {
        return heatPower;
    }

    @Override
    public void update(float bonusRate)
    {
        heat += getHeatPower();
        heat -= radiation;
        heat = MathHelper.clamp(heat, minHeat, getMaxHeat());
    }

    public void updateNearby(IHeatHandler heatHandler)
    {
        if (heatHandler == null)
        {
            setHeatPower(0);
            update(0);
            return;
        }
        if (heatHandler.getHeat() > getHeat())
        {
            float delta = MathHelper.sqrt(heatHandler.getHeat() - getHeat());
            addHeat(delta);
            setHeatPower(delta);
        }
        else
        {
            setHeatPower(0);
            setHeat(getHeat() - MathHelper.clamp(heatHandler.getHeatPower() + getRadiation(), 0, getHeat() - heatHandler.getHeat()));
        }
    }

    @Override public float getHeat()
    {
        return heat;
    }

    @Override public void setHeat(float heat)
    {
        this.heat = heat;
    }

    @Override
    public float getMinHeat()
    {
        return minHeat;
    }

    public void setMinHeat(float minHeat)
    {
        this.minHeat = minHeat;
    }

    @Override public float getMaxHeat()
    {
        return maxHeat;
    }

    public void setMaxHeat(float maxHeat)
    {
        this.maxHeat = maxHeat;
    }

    @Override public float getHeatPower()
    {
        return heatPower;
    }

    public void setHeatPower(float heatPower)
    {
        this.heatPower = heatPower;
    }

    public float getRadiation()
    {
        return radiation;
    }

    public void setRadiation(float radiation)
    {
        this.radiation = radiation;
    }
}
