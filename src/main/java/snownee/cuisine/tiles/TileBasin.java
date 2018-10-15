package snownee.cuisine.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.cuisine.api.process.BasinInteracting.Output;
import snownee.cuisine.api.process.CuisineProcessingRecipeManager;
import snownee.cuisine.api.process.Processing;

public class TileBasin extends TileInventoryBase
{
    public FluidTank tank = new FluidTank(8000)
    {
        @Override
        protected void onContentsChanged()
        {
            TileBasin.this.onContentsChanged(0);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid)
        {
            if (fluid == null || !canFill() || fluid.getFluid().isGaseous(fluid) || fluid.getFluid().isLighterThanAir())
            {
                return false;
            }
            return TileBasin.this.getClass() != TileBasin.class || fluid.getFluid().getTemperature(fluid) < 500;
        }
    };
    public int tickCheckThrowing = 0;
    private FluidStack liquidForRendering = null;

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
    public void onLoad()
    {
        super.onLoad();
        if (world.isRemote && tank.getFluid() != null)
        {
            liquidForRendering = tank.getFluid().copy();
        }
    }

    @SideOnly(Side.CLIENT)
    public FluidStack getFluidForRendering(float partialTicks)
    {
        final FluidStack actual = tank.getFluid();
        int actualAmount = 0;
        if (actual != null && !actual.equals(liquidForRendering))
        {
            liquidForRendering = new FluidStack(actual, 0);
        }
        if (liquidForRendering == null)
        {
            return null;
        }
        actualAmount = actual == null ? 0 : actual.amount;
        int delta = actualAmount - liquidForRendering.amount; // We assume your FPS is not that fast =w=
        if (Math.abs(delta) <= 40)
        {
            liquidForRendering.amount = actualAmount;
        }
        else
        {
            int i = (int) (delta * partialTicks * 0.1);
            if (i == 0) // Wow your PC is so powerful!
            {
                i = delta > 0 ? 1 : -1;
            }
            liquidForRendering.amount += i;
        }
        if (liquidForRendering.amount == 0)
        {
            liquidForRendering = null;
        }
        return liquidForRendering;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        tank.readFromNBT(compound.getCompoundTag("tank"));
    }

    @Nonnull
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
            Output output = recipe.getOutputAndConsumeInput(input, fluid, world.rand);
            if (output.fluid != null)
            {
                if (output.fluid.amount > tank.getCapacity())
                {
                    return;
                }
                if (output.fluid.amount <= 0)
                {
                    output.fluid = null;
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

    public FluidStack getCurrentFluidContent()
    {
        FluidStack content = this.tank.getFluid();
        if (content == null)
        {
            return null;
        }
        else
        {
            // Never assume people won't do bad things such as manipulating the return value
            return content.copy();
        }
    }

}
