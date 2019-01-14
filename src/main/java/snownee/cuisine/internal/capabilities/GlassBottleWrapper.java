package snownee.cuisine.internal.capabilities;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.fluids.CuisineFluids;
import snownee.cuisine.util.ItemNBTUtil;

@EventBusSubscriber(modid = Cuisine.MODID)
public class GlassBottleWrapper extends FluidHandlerItemStackSimple
{

    public GlassBottleWrapper(ItemStack stack)
    {
        super(stack, 250);
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid)
    {
        return fluid.getFluid() == FluidRegistry.WATER || fluid.getFluid() == CuisineFluids.JUICE || fluid.getFluid().getName().equals("milk");
    }

    @Override
    protected void setFluid(FluidStack fluid)
    {
        if (fluid.getFluid() == FluidRegistry.WATER)
        {
            container = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
        }
        else
        {
            container = new ItemStack(CuisineRegistry.BOTTLE);
            super.setFluid(fluid);
        }
    }

    @Override
    protected void setContainerToEmpty()
    {
        super.setContainerToEmpty();
        this.container = new ItemStack(Items.GLASS_BOTTLE);
    }

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        if (stack.getItem() instanceof ItemGlassBottle)
        {
            ResourceLocation resourceLocation = new ResourceLocation(Cuisine.MODID, "fluid_container");
            GlassBottleWrapper fluidHandler = new GlassBottleWrapper(stack);
            event.addCapability(resourceLocation, fluidHandler);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && ItemNBTUtil.verifyExistence(container, "potion"))
        {
            return false;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && ItemNBTUtil.verifyExistence(container, "potion"))
        {
            return null;
        }
        return super.getCapability(capability, facing);
    }
}
