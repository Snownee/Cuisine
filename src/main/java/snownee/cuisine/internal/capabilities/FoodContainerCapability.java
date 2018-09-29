package snownee.cuisine.internal.capabilities;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.prefab.SimpleFoodContainerImpl;
import snownee.cuisine.internal.CuisinePersistenceCenter;

public final class FoodContainerCapability
{

    private FoodContainerCapability()
    {
        throw new UnsupportedOperationException("No instance for you");
    }

    public static void init()
    {
        CapabilityManager.INSTANCE.register(FoodContainer.class, new Storage(), SimpleFoodContainerImpl::new);
    }

    static class Storage implements Capability.IStorage<FoodContainer>
    {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<FoodContainer> capability, FoodContainer instance, EnumFacing side)
        {
            CompositeFood object = instance.get();
            NBTTagCompound data;
            if (object == null)
            {
                data = new NBTTagCompound();
            }
            else
            {
                data = CuisinePersistenceCenter.serialize(object);
            }
            return data;
        }

        @Override
        public void readNBT(Capability<FoodContainer> capability, FoodContainer instance, EnumFacing side, NBTBase nbt)
        {
            if (nbt instanceof NBTTagCompound)
            {
                instance.set(CuisinePersistenceCenter.deserialize((NBTTagCompound) nbt));
            }
            else
            {
                Cuisine.logger.debug("FoodContainer capability expects a NBTTagCompound, but found {}. Assume empty.", nbt);
                instance.set(null);
            }
        }
    }
}
