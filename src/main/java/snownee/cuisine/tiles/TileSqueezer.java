package snownee.cuisine.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.animation.TimeValues;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.energy.CapabilityEnergy;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.api.process.Processing;

public class TileSqueezer extends TileBase implements ITickable
{

    private enum State
    {
        EXTRACTED, EXTENDING, EXTENDED, EXTRACTING
    }

    /**
     * The absolute value of farthest offset that "piston arm" can reach.
     */
    private static final float OFFSET_LIMIT = 0.78125F;
    /**
     * The step length of translation of "piston arm" when extending.
     */
    private static final int EXTENDING_UNIT_LENGTH = 25;
    /**
     * The step length of translation of "piston arm" when extracting.
     */
    private static final int EXTRACTING_UNIT_LENGTH = 10;
    /**
     * Location of animation state machine definition file used for squeezer.
     */
    private static final ResourceLocation STATE_MACHINE = new ResourceLocation(Cuisine.MODID, "asms/squeezer.json");

    private final IAnimationStateMachine stateMachine;

    private final TimeValues.VariableValue extensionOffset = new TimeValues.VariableValue(0F);

    private int extensionProgress;

    private State state = State.EXTRACTED;

    private boolean isInWorkCycle = false;

    private final Battery battery;

    public TileSqueezer()
    {
        this.stateMachine = Cuisine.sidedDelegate.loadAnimationStateMachine(STATE_MACHINE, ImmutableMap.of("offset", this.extensionOffset));
        if (CuisineConfig.GENERAL.squeezerUsesFE > 0)
        {
            battery = new Battery(CuisineConfig.GENERAL.squeezerUsesFE * 50, CuisineConfig.GENERAL.squeezerUsesFE, 0)
            {
                @Override
                protected void onEnergyChanged()
                {
                    refresh();
                }
            };
        }
        else
        {
            battery = null;
        }
    }

    @Override
    public void update()
    {
        boolean triggered = this.world.isBlockPowered(this.pos); //TODO: better implement
        if (battery != null)
        {
            triggered = !triggered;
        }
        if (battery == null)
        {
            if (triggered)
            {
                if (state == State.EXTRACTED || state == State.EXTRACTING)
                {
                    this.isInWorkCycle = this.state == State.EXTRACTED;
                    this.state = State.EXTENDING;
                    this.animationTransition("moving");
                }
            }
            else
            {
                if (state == State.EXTENDED || state == State.EXTENDING)
                {
                    this.state = State.EXTRACTING;
                    this.animationTransition("moving");
                }
            }
        }
        else
        {
            if (triggered)
            {
                if ((state == State.EXTRACTED || state == State.EXTENDING) && battery.getEnergyStored() < CuisineConfig.GENERAL.squeezerUsesFE)
                {
                    return;
                }
                if (state == State.EXTENDED)
                {
                    this.state = State.EXTRACTING;
                    this.animationTransition("moving");
                }
                else if (state == State.EXTRACTED)
                {
                    TileEntity tile = world.getTileEntity(pos.down());
                    if (tile instanceof TileBasin)
                    {
                        TileBasin tileBasin = (TileBasin) tile;
                        tileBasin.process(Processing.SQUEEZING, tileBasin.stacks.getStackInSlot(0), true);
                        if (tileBasin.squeezingFailed)
                        {
                            return;
                        }
                        this.isInWorkCycle = true;
                        this.state = State.EXTENDING;
                        this.animationTransition("moving");
                    }
                    else
                    {
                        return;
                    }
                }
            }
            else
            {
                return;
            }
        }

        if (this.state == State.EXTENDING)
        {
            extensionProgress += EXTENDING_UNIT_LENGTH;
            if (extensionProgress >= 100)
            {
                extensionProgress = 100;
                this.state = State.EXTENDED;
                if (!world.isRemote)
                {
                    world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() / 4 + .6F);
                }
            }
        }
        else if (this.state == State.EXTRACTING)
        {
            extensionProgress -= EXTRACTING_UNIT_LENGTH;
            if (extensionProgress <= 0)
            {
                extensionProgress = 0;
                if (world.isRemote)
                {
                    if (!"extracted".equals(this.stateMachine.currentState()))
                    {
                        this.animationTransition("extracted");
                    }
                }
                else
                {
                    world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() / 4 + 0.6F);
                }
                this.state = State.EXTRACTED;
            }
        }

        if (this.state == State.EXTENDED && this.isInWorkCycle)
        {
            this.isInWorkCycle = false;
            if (!world.isRemote)
            {
                TileEntity tile = this.world.getTileEntity(this.pos.down());
                if (tile instanceof TileBasin)
                {
                    TileBasin basin = (TileBasin) tile;
                    basin.process(Processing.SQUEEZING, basin.stacks.getStackInSlot(0), false);
                    if (battery != null)
                    {
                        battery.setEnergy(battery.getEnergyStored() - CuisineConfig.GENERAL.squeezerUsesFE);
                    }
                }
            }
        }

        this.updateOffset();
    }

    private void animationTransition(String newStateName)
    {
        if (this.world.isRemote)
        {
            this.stateMachine.transition(newStateName);
        }
    }

    private void updateOffset()
    {
        if (this.world.isRemote)
        {
            this.extensionOffset.setValue((extensionProgress + Animation.getPartialTickTime()) / 100F * OFFSET_LIMIT);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if (battery != null)
        {
            battery.readFromNBT(compound);
        }
        this.extensionProgress = compound.getInteger("Extension");
        this.state = State.values()[compound.getInteger("State")];
        this.isInWorkCycle = compound.getBoolean("WorkCycle");
        /*
         * You might want to ask why we don't do this in onLoad. The cruel fact is that,
         * TileEntity.handleUpdateTag is called after TileEntity.onLoad, where we get
         * the correct data used for animation. When onLoad is called, the data on
         * client is still incorrect (i.e. not the data received from server after chunk
         * loaded and the chunk data are synced over).
         * See TileMill.readFromNBT for a similar example.
         */
        if (this.world != null && this.world.isRemote)
        {
            if (this.state != State.EXTRACTED)
            {
                this.updateOffset();
                if (!"moving".equals(this.stateMachine.currentState()))
                {
                    this.stateMachine.transition("moving");
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        if (battery != null)
        {
            battery.writeToNBT(compound);
        }
        compound.setInteger("Extension", this.extensionProgress);
        compound.setInteger("State", this.state.ordinal());
        compound.setBoolean("WorkCycle", this.isInWorkCycle);
        return super.writeToNBT(compound);
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        if (battery != null)
        {
            battery.readFromNBT(data);
        }
        this.extensionProgress = data.getInteger("Extension");
        this.state = State.values()[data.getInteger("State")];
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        if (battery != null)
        {
            battery.writeToNBT(data);
        }
        data.setInteger("Extension", this.extensionProgress);
        data.setInteger("State", this.state.ordinal());
        return data;
    }

    @Override
    public boolean hasFastRenderer()
    {
        return true;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityAnimation.ANIMATION_CAPABILITY || (capability == CapabilityEnergy.ENERGY && battery != null) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
        {
            return CapabilityAnimation.ANIMATION_CAPABILITY.cast(this.stateMachine);
        }
        else if (capability == CapabilityEnergy.ENERGY && battery != null)
        {
            return CapabilityEnergy.ENERGY.cast(battery);
        }
        else
        {
            return super.getCapability(capability, facing);
        }
    }
}
