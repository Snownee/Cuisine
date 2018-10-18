package snownee.cuisine.tiles;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockDispenser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.animation.TimeValues;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import snownee.cuisine.Cuisine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class TileSqueezer extends TileBase implements ITickable
{

    /**
     * Private enum used for denoting the "piston arm" state of squeezer.
     */
    enum State
    {
        EXTRACTED, EXTENDING, EXTENDED, EXTRACTING;

        final String stateName = this.name().toLowerCase(Locale.ENGLISH);
    }

    /**
     * The absolute value of farthest offset that "piston arm" can reach.
     */
    private static final float OFFSET_LIMIT = 0.78125F;

    /**
     * Location of animation state machine definition file used for squeezer.
     */
    private static final ResourceLocation STATE_MACHINE = new ResourceLocation(Cuisine.MODID, "asms/squeezer.json");

    private IAnimationStateMachine stateMachine;

    private TimeValues.VariableValue extensionOffset = new TimeValues.VariableValue(0F);

    private int extensionProgress;

    private State state = State.EXTRACTED;

    public TileSqueezer()
    {
        this.stateMachine = Cuisine.proxy.loadAnimationStateMachine(STATE_MACHINE, ImmutableMap.of("offset", this.extensionOffset));
    }

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            return;
        }

        if (this.state == State.EXTENDING && State.EXTENDING.stateName.equals(this.stateMachine.currentState()))
        {
            extensionProgress += 10;
            if (extensionProgress >= 100)
            {
                //extensionProgress = 100;
                stateMachine.transition(State.EXTENDED.stateName);
                state = State.EXTENDED;
            }
            else
            {
                extensionOffset.setValue((extensionProgress + Animation.getPartialTickTime()) / 100F * OFFSET_LIMIT);
            }
        }
        else if (this.state == State.EXTRACTING && State.EXTRACTING.stateName.equals(this.stateMachine.currentState()))
        {
            extensionProgress -= 10;
            if (extensionProgress <= 0)
            {
                extensionProgress = 0;
                stateMachine.transition(State.EXTRACTED.stateName);
                state = State.EXTRACTED;
            }
            else
            {
                extensionOffset.setValue((extensionProgress + Animation.getPartialTickTime()) / 100F * OFFSET_LIMIT);
            }
        }
    }

    public void startExtending()
    {
        if (this.state == State.EXTRACTED || this.state == State.EXTRACTING)
        {
            this.state = State.EXTENDING;
            if (!State.EXTENDING.stateName.equals(this.stateMachine.currentState()))
            {
                this.stateMachine.transition(State.EXTENDING.stateName);
            }
        }
    }

    public void startExtracting()
    {
        if (this.state == State.EXTENDED || this.state == State.EXTENDING)
        {
            this.state = State.EXTRACTING;
            if (!State.EXTRACTING.stateName.equals(this.stateMachine.currentState()))
            {
                this.stateMachine.transition(State.EXTRACTING.stateName);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.extensionProgress = compound.getInteger("Extension");
        this.state = State.values()[compound.getInteger("state")];
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("Extension", this.extensionProgress);
        compound.setInteger("state", this.state.ordinal());
        return super.writeToNBT(compound);
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        boolean triggered = this.world.getBlockState(this.pos).getValue(BlockDispenser.TRIGGERED);
        if (triggered)
        {
            this.startExtending();
        }
        else
        {
            this.startExtracting();
        }
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
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
        return capability == CapabilityAnimation.ANIMATION_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
        {
            return CapabilityAnimation.ANIMATION_CAPABILITY.cast(this.stateMachine);
        }
        else
        {
            return super.getCapability(capability, facing);
        }
    }
}
