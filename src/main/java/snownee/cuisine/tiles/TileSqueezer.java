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

        final String stateName;

        State()
        {
            stateName = this.name().toLowerCase(Locale.ENGLISH);
        }
    }

    /**
     * The farthest offset that "piston arm" can reach. Negative means it moves
     * on negative direction, and in this case it is down direction (negative
     * y-axis).
     */
    private static final float OFFSET_LIMIT = -0.78125F;

    /**
     * Location of animation state machine definition file used for squeezer.
     */
    private static final ResourceLocation STATE_MACHINE = new ResourceLocation(Cuisine.MODID, "asms/squeezer.json");

    private IAnimationStateMachine stateMachine;

    private TimeValues.VariableValue extensionOffset = new TimeValues.VariableValue(0F);

    private int extensionProgress;

    private boolean triggered;

    private State state = State.EXTRACTED;

    public TileSqueezer()
    {
        this.stateMachine = Cuisine.proxy.loadAnimationStateMachine(STATE_MACHINE, ImmutableMap.of("offset", this.extensionOffset));
    }

    public void onTriggered(boolean triggered)
    {
        this.triggered = triggered;
    }

    @Override
    public void update()
    {
        switch (state)
        {
            case EXTENDING:
            {
                extensionProgress += 5;
                extensionOffset.setValue(+ extensionProgress / 100F * OFFSET_LIMIT);
                if (extensionProgress >= 100)
                {
                    extensionProgress = 100;
                    stateMachine.transition(State.EXTENDED.stateName);
                    state = State.EXTENDED;
                }
                break;
            }
            case EXTRACTING:
            {
                extensionProgress -= 5;
                extensionOffset.setValue(- extensionProgress / 100F * OFFSET_LIMIT);
                if (extensionProgress <= 0)
                {
                    extensionProgress = 0;
                    stateMachine.transition(State.EXTRACTED.stateName);
                    state = State.EXTRACTED;
                }
                break;
            }
        }
    }

    public void startExtending()
    {
        if (this.state == State.EXTRACTED || this.state == State.EXTRACTING)
        {
            this.state = State.EXTENDING;
        }
    }

    public void startExtracting()
    {
        if (this.state == State.EXTENDED || this.state == State.EXTENDING)
        {
            this.state = State.EXTRACTING;
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
        this.triggered = this.world.getBlockState(this.pos).getValue(BlockDispenser.TRIGGERED);
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
        //data.setBoolean("triggered", this.triggered);
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
