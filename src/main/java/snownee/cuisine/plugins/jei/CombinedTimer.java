package snownee.cuisine.plugins.jei;

import mezz.jei.api.gui.ITickTimer;
import net.minecraft.util.math.MathHelper;

public final class CombinedTimer implements ITickTimer
{
    private final int msPerCycle;
    private final int startValue;
    private final int width;
    private final int maxValue;
    private long startTime;

    CombinedTimer(int ticksPerCycle, int startValue, int width, int maxValue)
    {
        this.msPerCycle = ticksPerCycle * 50;
        this.startValue = startValue;
        this.width = width;
        this.maxValue = maxValue;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public int getValue()
    {
        long msPassed = (System.currentTimeMillis() - startTime) % msPerCycle;
        int value = (int) Math.floorDiv(msPassed * (maxValue + 1), msPerCycle) - startValue;
        return width - MathHelper.clamp(value, 0, width);
    }

    @Override
    public int getMaxValue()
    {
        return width;
    }

}
