package snownee.cuisine.items;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import snownee.cuisine.internal.capabilities.GlassBottleWrapper;
import snownee.kiwi.item.ItemMod;

public class ItemBottle extends ItemMod
{

    public ItemBottle(String name)
    {
        super(name);
        setContainerItem(Items.GLASS_BOTTLE);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return new GlassBottleWrapper(stack);
    }
}
