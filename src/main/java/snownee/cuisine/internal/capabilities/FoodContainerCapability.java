package snownee.cuisine.internal.capabilities;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.prefab.SimpleFoodContainerImpl;
import snownee.cuisine.internal.CuisineSharedSecrets;

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
            CompositeFood food = instance.get();
            NBTTagCompound data;
            if (food == null)
            {
                data = new NBTTagCompound();
            }
            else
            {
                data = CulinaryHub.API_INSTANCE.serialize(food);
            }
            return data;
        }

        @Override
        public void readNBT(Capability<FoodContainer> capability, FoodContainer instance, EnumFacing side, NBTBase nbt)
        {
            if (nbt instanceof NBTTagCompound)
            {
                NBTTagCompound data = ((NBTTagCompound)nbt).getCompoundTag("dish");
                ResourceLocation id = new ResourceLocation(data.getString(CuisineSharedSecrets.KEY_TYPE));
                instance.set(CulinaryHub.API_INSTANCE.deserialize(id, data));
                if (instance.get() == null)
                {
                    instance.set(CuisinePersistenceCenter.deserialize(data));
                }
            }
            else
            {
                Cuisine.logger.debug("FoodContainer capability expects a NBTTagCompound, but found {}. Assume empty.", nbt);
                instance.set(null);
            }
        }
    }
}
