package snownee.cuisine.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import snownee.kiwi.tile.TileBase;

public class TileFirePit extends TileBase
{
    public final FuelHeatHandler heatHandler;

    public TileFirePit()
    {
        heatHandler = new FuelHeatHandler();
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        if (data.hasKey("heat", Constants.NBT.TAG_FLOAT))
        {
            heatHandler.setHeat(data.getFloat("heat"));
        }
        super.readFromNBT(data);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data)
    {
        data.setFloat("heat", heatHandler.getHeat());
        return super.writeToNBT(data);
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        if (data.hasKey("heat", Constants.NBT.TAG_FLOAT))
        {
            heatHandler.setHeat(data.getFloat("heat"));
        }
    }

    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setFloat("heat", heatHandler.getHeat());
        return data;
    }

}
