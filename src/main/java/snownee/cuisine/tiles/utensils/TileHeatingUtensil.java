package snownee.cuisine.tiles.utensils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import snownee.cuisine.api.IHeatHandler;
import snownee.cuisine.api.IHeatable;
import snownee.cuisine.tiles.heat.HeatHandler;

import javax.annotation.Nonnull;

public abstract class TileHeatingUtensil extends TileUtensil implements IHeatable
{
    protected HeatHandler heatHandler;
    protected IHeatHandler heaterHandler;

    public TileHeatingUtensil(HeatHandler heatHandler)
    {
        this.heatHandler = heatHandler;
    }

    @Override
    public HeatHandler getHeatHandler()
    {
        return heatHandler;
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        if (data.hasKey("heat", Constants.NBT.TAG_FLOAT))
        {
            heatHandler.setHeat(data.getFloat("heat"));
        }
        super.readFromNBT(data);
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setFloat("heat", heatHandler.getHeat());
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        if (data.hasKey("heat", Constants.NBT.TAG_FLOAT))
            heatHandler.setHeat(data.getFloat("heat"));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data)
    {
        data.setFloat("heat", heatHandler.getHeat());
        return super.writeToNBT(data);
    }

    @Override
    public void update()
    {
        heatHandler.updateNearby(heaterHandler);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        refresh();
    }

    @Override
    protected void refresh()
    {
        super.refresh();
        // https://minecraft.gamepedia.com/Biome#Temperature
        heatHandler.setMinHeat(getWorld().getBiome(getPos()).getTemperature(getPos()) * 28);
    }

    public void updateNearby(IHeatable heater)
    {
        if (heater == null)
            heaterHandler = null;
        else
            heaterHandler = heater.getHeatHandler();
        heatHandler.setMinHeat(getWorld().getBiome(getPos()).getTemperature(getPos()) * 28);
    }
}
