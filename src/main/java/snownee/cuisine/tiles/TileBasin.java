package snownee.cuisine.tiles;

import javax.annotation.Nullable;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.cuisine.api.process.BasinInteracting.Output;
import snownee.cuisine.api.process.CuisineProcessingRecipeManager;
import snownee.cuisine.api.process.Processing;

public class TileBasin extends TileInventoryBase
{
    private FluidTank tank = new FluidTank(8000);
    public int tickCheckThrowing = 0;

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

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        tank.readFromNBT(compound.getCompoundTag("tank"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        return compound;
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
            Output output = recipe.getOutputAndConsumeInput(input, fluid);
            if (output.fluid != null)
            {
                if (output.fluid.amount > tank.getCapacity())
                {
                    return;
                }
                tank.setFluid(output.fluid);
            }
            if (!output.item.isEmpty())
            {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), output.item);
            }
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return BasinInteracting.isKnownInput(Processing.SQUEEZING, stack);
    }

}
