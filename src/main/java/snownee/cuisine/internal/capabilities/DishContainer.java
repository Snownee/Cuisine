package snownee.cuisine.internal.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.prefab.SimpleFoodContainerImpl;
import snownee.cuisine.internal.CuisineSharedSecrets;

/**
 * The {@link snownee.cuisine.api.FoodContainer} implementation used by
 * {@link snownee.cuisine.items.ItemDish}.
 */
public class DishContainer extends SimpleFoodContainerImpl implements ICapabilityProvider, INBTSerializable<NBTTagCompound>
{

    @Nonnull
    @Override
    public ItemStack getEmptyContainer(ItemStack currentContainer)
    {
        return new ItemStack(CuisineRegistry.PLACED_DISH);
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
            return CulinaryHub.API_INSTANCE.serialize(this.food.getIdentifier(), this.food);
        }
    }

    @Override
    public void deserializeNBT(NBTTagCompound data)
    {
        ResourceLocation id = new ResourceLocation(data.getString(CuisineSharedSecrets.KEY_TYPE));
        this.food = CulinaryHub.API_INSTANCE.deserialize(id, data);
    }
}
