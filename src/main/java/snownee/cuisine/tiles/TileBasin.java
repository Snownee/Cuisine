package snownee.cuisine.tiles;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.cuisine.api.process.CuisineProcessingRecipeManager;

public class TileBasin extends TileInventoryBase
{
    private FluidTank tank = new FluidTank(4000);

    public TileBasin()
    {
        super(1);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
        }
        return super.getCapability(capability, facing);
    }

    public void process(CuisineProcessingRecipeManager<BasinInteracting> recipeManager, ItemStack input)
    {
        if (input.isEmpty())
        {
            return;
        }
        FluidStack fluid = tank.getFluid();
        BasinInteracting recipe = recipeManager.findRecipe(input, fluid);
        if (recipe != null)
        {
            FluidStack output = recipe.getOutput(input, fluid);
            if (output == null)
            {
                return;
            }
            if (output.amount > tank.getCapacity())
            {
                output.amount = tank.getCapacity();
            }
            tank.setFluid(output);
        }
    }

}
