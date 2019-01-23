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
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.fluids.CuisineFluids;
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.NBTHelper.Tag;

@EventBusSubscriber(modid = Cuisine.MODID)
public class GlassBottleHandler
{
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
        else if (CuisineConfig.GENERAL.attachWaterBottleCapability && stack.getItem() == Items.POTIONITEM)
        {
            ResourceLocation resourceLocation = new ResourceLocation(Cuisine.MODID, "fluid_container");
            WaterBottleWrapper fluidHandler = new WaterBottleWrapper(stack);
            event.addCapability(resourceLocation, fluidHandler);
        }
    }

    public static class GlassBottleWrapper extends FluidHandlerItemStackSimple
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

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && NBTHelper.of(container).hasTag("potion", Tag.STRING))
            {
                return false;
            }
            return super.hasCapability(capability, facing);
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing)
        {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && NBTHelper.of(container).hasTag("potion", Tag.STRING))
            {
                return null;
            }
            return super.getCapability(capability, facing);
        }
    }

    public static class WaterBottleWrapper extends FluidHandlerItemStackSimple
    {
        public WaterBottleWrapper(ItemStack stack)
        {
            super(stack, 250);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid)
        {
            return false;
        }

        @Override
        public FluidStack getFluid()
        {
            return new FluidStack(FluidRegistry.WATER, 250);
        }

        @Override
        protected void setFluid(FluidStack fluid)
        {
        }

        @Override
        protected void setContainerToEmpty()
        {
            super.setContainerToEmpty();
            this.container = new ItemStack(Items.GLASS_BOTTLE);
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && PotionUtils.getPotionFromItem(container) != PotionTypes.WATER)
            {
                return false;
            }
            return super.hasCapability(capability, facing);
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing)
        {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && PotionUtils.getPotionFromItem(container) != PotionTypes.WATER)
            {
                return null;
            }
            return super.getCapability(capability, facing);
        }
    }
}
