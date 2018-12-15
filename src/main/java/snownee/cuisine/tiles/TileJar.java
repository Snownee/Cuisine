package snownee.cuisine.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.process.Vessel;
import snownee.cuisine.network.PacketCustomEvent;
import snownee.kiwi.network.NetworkChannel;
import snownee.kiwi.util.InventoryUtil;

public class TileJar extends TileInventoryBase implements ITickable
{

    private static final int TOTAL_TICK = 1200;

    private int processTime = 0;
    private boolean isWorking = false;
    private boolean invLock = false;

    private final FluidTank tank = new FluidTank(10000)
    {
        @Override
        protected void onContentsChanged()
        {
            resetProcessing();
        }
    };

    public TileJar()
    {
        super(5);
        tank.setTileEntity(this);
    }

    public FluidTank getTank()
    {
        return tank;
    }

    private Object[][] populateInputs()
    {
        Object[][] inputs = new Object[stacks.getSlots()][this.stacks.getSlots() + 1];
        for (int i = 0; i < stacks.getSlots(); i++)
        {
            inputs[i] = populateInputs(i);
        }
        return inputs;
    }

    private Object[] populateInputs(int slot)
    {
        Object[] inputs = new Object[this.stacks.getSlots() + 1];
        inputs[0] = tank.getFluid();
        inputs[1] = stacks.getStackInSlot(slot);
        for (int j = 0; j < stacks.getSlots(); j++)
        {
            if (j != slot)
            {
                inputs[j + ((j < slot) ? 2 : 1)] = stacks.getStackInSlot(j);
            }
        }
        return inputs;
    }

    @Override
    public void onContentsChanged(int slot)
    {
        if (!invLock)
        {
            resetProcessing();
        }
    }

    public void spillFluids()
    {
        FluidEvent.fireEvent(new FluidEvent.FluidSpilledEvent(tank.getFluid(), world, pos));
    }

    public Vessel findCurrentRecipe()
    {
        Vessel recipe = null;
        for (Object[] inputs : populateInputs())
        {
            if ((recipe = Processing.VESSEL.findRecipe(inputs)) != null)
            {
                break;
            }
        }
        return recipe;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
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

    // @SideOnly(Side.CLIENT) // Left commented so we know that this is for client only
    public void forceSetWorkingStatus(boolean working)
    {
        this.isWorking = working;
        processTime = working ? TOTAL_TICK : 0;
    }

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            if (isWorking && ++processTime % TOTAL_TICK == 0)
            {
                invLock = true;
                for (int i = 0; i < stacks.getSlots(); i++)
                {
                    ItemStack stack = stacks.getStackInSlot(i);
                    int count = stack.getCount();
                    if (stack.isEmpty() || count > processTime / TOTAL_TICK)
                    {
                        continue;
                    }
                    Vessel recipe = Processing.VESSEL.findRecipe(populateInputs(i));
                    if (recipe == null)
                    {
                        continue;
                    }
                    ItemStack output = recipe.getOutput().getItemStack();
                    output.setCount(count);
                    stacks.setStackInSlot(i, output); // TODO (Snownee): merge stacks
                    InventoryUtil.consumeItemStack(stacks, recipe.getExtraRequirement(), count, false);
                    FluidStack outputFluid = recipe.getOutputFluid();
                    if (outputFluid != null)
                    {
                        outputFluid = outputFluid.copy();
                        outputFluid.amount *= tank.getFluidAmount() / 100;
                        outputFluid.amount = MathHelper.clamp(outputFluid.amount, 0, tank.getCapacity());
                        tank.setFluid(outputFluid);
                    }
                }
                invLock = false;
                resetProcessing();
                world.updateComparatorOutputLevel(this.pos, this.blockType);
            }
        }
        else if (isWorking)
        {
            if (--processTime > 0 && processTime % 10 == 0)
            {
                double y = pos.getY() + 0.12D;
                float f = (float) (processTime / (double) TOTAL_TICK * Math.PI * 2);
                double x = pos.getX() + 0.5D + MathHelper.sin(f) * 0.7;
                double z = pos.getZ() + 0.5D + MathHelper.cos(f) * 0.7;
                world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x, y, z, 0, 0.07, 0);
            }
        }
    }

    public void resetProcessing()
    {
        if (hasWorld() && !world.isRemote)
        {
            Vessel recipe = findCurrentRecipe();
            boolean lastWorking = this.isWorking;
            this.isWorking = recipe != null;
            if (!this.isWorking)
            {
                processTime = 0;
            }
            if (isWorking || !lastWorking)
            {
                NetworkChannel.INSTANCE.sendToDimension(new PacketCustomEvent(5, this.pos, isWorking ? 1 : 0), this.getWorld().provider.getDimension());
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        invLock = true;
        super.readFromNBT(compound);
        isWorking = compound.getBoolean("working");
        processTime = compound.getInteger("progress");
        tank.readFromNBT(compound.getCompoundTag("tank"));
        invLock = false;
        resetProcessing();
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setBoolean("working", this.isWorking);
        compound.setInteger("progress", this.processTime);
        compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        return compound;
    }
}
