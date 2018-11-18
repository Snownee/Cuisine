package snownee.cuisine.tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.animation.TimeValues;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.api.CulinarySkillPoint;
import snownee.cuisine.api.process.Milling;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.util.SkillUtil;
import snownee.cuisine.library.FilterFluidHandler;
import snownee.cuisine.library.FilterItemHandler;
import snownee.cuisine.library.SingleSlotItemHandler;
import snownee.cuisine.util.StacksUtil;

public class TileMill extends TileBase implements ITickable
{

    /**
     * The index of the sole slot of input buffer of this.
     */
    private static final int ITEM_SLOT = 0;
    /**
     * Location of definition JSON file of animation state machine used by TileMill.
     */
    private static final ResourceLocation STATE_MACHINE_JSON = new ResourceLocation(Cuisine.MODID, "asms/mill.json");

    private final IAnimationStateMachine stateMachine;
    private final TimeValues.VariableValue progressValue = new TimeValues.VariableValue(0F);

    /**
     * The signal used for indicating its working status.
     */
    private boolean working = false;
    /**
     * Current working progress, if {@link #working} is true.
     */
    private int tick = 0;

    private final SingleSlotItemHandler inputBuffer = new SingleSlotItemHandler();
    private final FluidTank fluidInput = new FluidTank(Fluid.BUCKET_VOLUME);
    private final FluidTank fluidOutput = new FluidTank(Fluid.BUCKET_VOLUME);

    public TileMill()
    {
        stateMachine = Cuisine.sidedDelegate.loadAnimationStateMachine(STATE_MACHINE_JSON, ImmutableMap.of("progress", progressValue));
        fluidInput.setTileEntity(this);
        fluidOutput.setTileEntity(this);
    }

    public void spillFluids()
    {
        FluidEvent.fireEvent(new FluidEvent.FluidSpilledEvent(fluidInput.getFluid(), world, pos));
        FluidEvent.fireEvent(new FluidEvent.FluidSpilledEvent(fluidOutput.getFluid(), world, pos));
    }

    public void onRightClicked(EntityPlayer playerIn)
    {
        if (this.world.isRemote)
        {
            return;
        }

        if (!this.working)
        {
            playerIn.addExhaustion(5);
            SkillUtil.increasePoint(playerIn, CulinarySkillPoint.PROFICIENCY, 1);
            this.working = true;
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 1 | 2);
        }
    }

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            if (working)
            {
                if (++tick >= CuisineConfig.GENERAL.millWorkPeriod)
                {
                    process();
                    IBlockState state = this.world.getBlockState(this.pos);
                    this.world.updateComparatorOutputLevel(this.pos, this.blockType);
                    this.working = false;
                    this.tick = 0;
                    this.markDirty(); // Think the "flush()" call when dealing with output stream
                    this.world.notifyBlockUpdate(this.pos, state, state, 1 | 2);
                }
            }
        }
        else
        {
            if (working)
            {
                this.tick = Math.min(CuisineConfig.GENERAL.millWorkPeriod, this.tick + 1);
                if (tick < CuisineConfig.GENERAL.millWorkPeriod)
                {
                    progressValue.setValue((this.tick + Animation.getPartialTickTime()) / CuisineConfig.GENERAL.millWorkPeriod);
                }
            }
            else
            {
                this.tick = 0;
            }
        }
    }

    private void process()
    {
        Milling recipe = Processing.MILLING.findRecipe(this.inputBuffer.getRawContent(), this.fluidInput.getTankProperties()[0].getContents());
        if (recipe != null)
        {
            if (recipe.getOutputFluid() != null && this.fluidOutput.fill(recipe.getOutputFluid(), false) != recipe.getOutputFluid().amount)
            {
                return;
            }

            this.inputBuffer.getRawContent().shrink(recipe.getInput().count());
            this.fluidInput.drain(recipe.getInputFluid(), true);

            if (!recipe.getOutput().isEmpty())
            {
                ItemStack finalOutput = recipe.getOutput().copy();
                for (EnumFacing direction : EnumSet.complementOf(EnumSet.of(EnumFacing.UP)))
                {
                    TileEntity target = this.world.getTileEntity(this.pos.offset(direction));
                    if (target == null || target.getClass() == TileEntityDispenser.class)
                    {
                        continue; // Ignore non-exist tile entity & Dispenser // Is there a better way to do this?
                    }
                    IItemHandler targetInv = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite());
                    if (targetInv == null)
                    {
                        continue; // Ignore side that has no IItemHandler support
                    }
                    for (int i = 0; i < targetInv.getSlots(); i++)
                    {
                        finalOutput = targetInv.insertItem(i, finalOutput, false);
                        if (finalOutput.isEmpty())
                        {
                            break; // Stop when all items are distributed
                        }
                    }
                    if (finalOutput.isEmpty())
                    {
                        break; // Stop when all items are distributed
                    }
                }
                if (!finalOutput.isEmpty()) // And eject the remainder to the world
                {
                    StacksUtil.spawnItemStack(this.getWorld(), this.getPos(), finalOutput, true);
                }
            }

            if (recipe.getOutputFluid() != null)
            {
                this.fluidOutput.fill(recipe.getOutputFluid().copy(), true);
            }
        }
    }

    // It's only here for BlockMill to quickly access TileMill's inventory
    // NOT FOR PUBLIC USE! This method is subject to change at ANY time.
    // Why not checking inputBuffer.isValidInput? because that would be
    // essentially the Milling.isKnownMillingInput call.
    public ItemStack tryInsertItem(ItemStack input)
    {
        if (Milling.isKnownMillingInput(input))
        {
            return this.inputBuffer.insertItem(ITEM_SLOT, input, false);
        }
        else
        {
            return input;
        }
    }

    public ItemStack getItemContent()
    {
        return this.inputBuffer.getRawContent();
    }

    @Nonnull
    @Override
    public NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setBoolean("working", this.working);
        return data;
    }

    @Override
    public void readPacketData(NBTTagCompound data)
    {
        this.working = data.getBoolean("working");
        if (working)
        {
            if ("halt".equals(stateMachine.currentState()))
            {
                stateMachine.transition("working");
            }
        }
        else
        {
            if ("working".equals(stateMachine.currentState()))
            {
                stateMachine.transition("halt");
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("item", inputBuffer.getRawContent().serializeNBT());
        compound.setTag("fluidInput", fluidInput.writeToNBT(new NBTTagCompound()));
        compound.setTag("fluidOutput", fluidOutput.writeToNBT(new NBTTagCompound()));
        compound.setInteger("progress", tick);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.inputBuffer.setRawContent(new ItemStack(compound.getCompoundTag("item")));
        fluidInput.readFromNBT(compound.getCompoundTag("fluidInput"));
        fluidOutput.readFromNBT(compound.getCompoundTag("fluidOutput"));
        tick = compound.getInteger("progress");
        working = tick > 0;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityAnimation.ANIMATION_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
        {
            return CapabilityAnimation.ANIMATION_CAPABILITY.cast(this.stateMachine);
        }
        else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            // Use a filter decorator to make sure we filter the input in advance
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new FilterItemHandler(this.inputBuffer, TileMill::isItemValid));
        }
        else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if (facing == EnumFacing.DOWN)
            {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.fluidOutput);
            }
            else
            {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new MillFluidHandler(new FilterFluidHandler(this.fluidInput, Milling::isKnownMillingInput), this.fluidOutput));
            }
        }
        else
        {
            return super.getCapability(capability, facing);
        }
    }

    @Override
    public boolean hasFastRenderer()
    {
        return true; // AnimationTESR is FastTESR
    }

    private static boolean isItemValid(int slot, ItemStack input)
    {
        return !input.isEmpty() && Milling.isKnownMillingInput(input);
    }

    /**
     * A decorator designed for Mill. It is used for exposing correct behaviors
     * via capability.
     *
     * 为磨设计的 IFluidHandler 的 wrapper，用于将正确的行为通过 Capability 暴露出来。
     */
    private static final class MillFluidHandler implements IFluidHandler
    {

        private final IFluidHandler input, output;

        MillFluidHandler(IFluidHandler input, IFluidHandler output)
        {
            this.input = input;
            this.output = output;
        }

        @Override
        public IFluidTankProperties[] getTankProperties()
        {
            ArrayList<IFluidTankProperties> properties = new ArrayList<>();
            Collections.addAll(properties, input.getTankProperties());
            for (IFluidTankProperties outputProperties : output.getTankProperties())
            {
                properties.add(new OutputOnlyTankProperties(outputProperties));
            }
            return properties.toArray(new IFluidTankProperties[0]);
        }

        @Override
        public int fill(FluidStack resource, boolean doFill)
        {
            return input.fill(resource, doFill);
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            FluidStack attemptDrainOutput = output.drain(resource, doDrain);
            if (attemptDrainOutput == null)
            {
                return input.drain(resource, doDrain);
            }
            else
            {
                return attemptDrainOutput;
            }
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            FluidStack attemptDrainOutput = output.drain(maxDrain, doDrain);
            if (attemptDrainOutput == null)
            {
                return input.drain(maxDrain, doDrain);
            }
            else
            {
                return attemptDrainOutput;
            }
        }

        private static final class OutputOnlyTankProperties implements IFluidTankProperties
        {
            private final IFluidTankProperties parent;

            OutputOnlyTankProperties(IFluidTankProperties parent)
            {
                this.parent = parent;
            }

            @Override
            @Nullable
            public FluidStack getContents()
            {
                return parent.getContents();
            }

            @Override
            public int getCapacity()
            {
                return parent.getCapacity();
            }

            @Override
            public boolean canFill()
            {
                return false;
            }

            @Override
            public boolean canDrain()
            {
                return parent.canDrain();
            }

            @Override
            public boolean canFillFluidType(FluidStack fluidStack)
            {
                return false;
            }

            @Override
            public boolean canDrainFluidType(FluidStack fluidStack)
            {
                return parent.canDrainFluidType(fluidStack);
            }
        }
    }

}
