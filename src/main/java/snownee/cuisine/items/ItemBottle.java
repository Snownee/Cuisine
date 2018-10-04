package snownee.cuisine.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack)
    {
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack);
        if (handler != null)
        {
            FluidStack fluid = handler.drain(Integer.MAX_VALUE, false);
            if (fluid != null)
            {
                return I18n.format(getTranslationKey(stack) + ".name", fluid.getLocalizedName());
            }
        }
        return super.getItemStackDisplayName(stack);
    }
}
