package snownee.cuisine.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.EnergyStorage;

//TODO (Snownee): Move to Kiwi
public class EnergyCell extends EnergyStorage
{

    public EnergyCell(int capacity)
    {
        super(capacity);
    }

    public EnergyCell(int capacity, int maxTransfer)
    {
        super(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyCell(int capacity, int maxReceive, int maxExtract)
    {
        super(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyCell(int capacity, int maxReceive, int maxExtract, int energy)
    {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public EnergyCell readFromNBT(NBTTagCompound nbt)
    {
        if (nbt.hasKey("Energy", Constants.NBT.TAG_INT))
        {
            energy = nbt.getInteger("Energy");
        }
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if (energy > 0)
        {
            nbt.setInteger("Energy", energy);
        }
        return nbt;
    }

    public void setEnergy(int energy)
    {
        this.energy = MathHelper.clamp(energy, 0, getMaxEnergyStored());
    }

}
