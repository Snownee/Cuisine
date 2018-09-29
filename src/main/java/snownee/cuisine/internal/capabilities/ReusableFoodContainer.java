package snownee.cuisine.internal.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.prefab.SimpleFoodContainerImpl;
import snownee.cuisine.internal.CuisinePersistenceCenter;

public class ReusableFoodContainer extends SimpleFoodContainerImpl
        implements ICapabilityProvider, INBTSerializable<NBTTagCompound>
{

    private ItemStack emptyContainer;

    public ReusableFoodContainer(final ItemStack empty)
    {
        this.emptyContainer = empty;
    }

    @Nonnull
    @Override
    public ItemStack getEmptyContainer(ItemStack currentContainer)
    {
        return emptyContainer;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CulinaryCapabilities.FOOD_CONTAINER;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CulinaryCapabilities.FOOD_CONTAINER)
        {
            return CulinaryCapabilities.FOOD_CONTAINER.cast(this);
        }
        else
        {
            return null;
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        if (this.food == null)
        {
            return new NBTTagCompound();
        }
        else
        {
            return CuisinePersistenceCenter.serialize(this.food);
        }
    }

    @Override
    public void deserializeNBT(NBTTagCompound data)
    {
        this.food = CuisinePersistenceCenter.deserialize(data);
    }
}
