package snownee.cuisine.tiles;

import com.google.common.collect.ImmutableMap;
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

public class TileSqueezer extends TileBase implements ITickable
{

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

    private IAnimationStateMachine stateMachine;

    private TimeValues.VariableValue extensionOffset = new TimeValues.VariableValue(0F);

    private int extensionProgress;

    private boolean working = false, pushing = false;

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

        boolean triggered = this.world.isBlockPowered(this.pos);
        if (triggered && !working)
        {
            working = true;
            this.stateMachine.transition("moving");
        }
        this.pushing = triggered;

        if (working)
        {
            if (this.pushing)
            {
                extensionProgress += EXTENDING_UNIT_LENGTH;
                if (extensionProgress >= 100)
                {
                    extensionProgress = 100;
                }
            }
            else
            {
                extensionProgress -= EXTRACTING_UNIT_LENGTH;
                if (extensionProgress <= 0)
                {
                    extensionProgress = 0;
                    if (!"extracted".equals(this.stateMachine.currentState()))
                    {
                        this.stateMachine.transition("extracted");
                    }
                    this.working = false;
                }
            }
        }
        extensionOffset.setValue((extensionProgress + Animation.getPartialTickTime()) / 100F * OFFSET_LIMIT);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.extensionProgress = compound.getInteger("Extension");
        this.pushing = compound.getBoolean("Pushing");
        this.working = compound.getBoolean("Working");
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("Extension", this.extensionProgress);
        compound.setBoolean("Pushing", this.pushing);
        compound.setBoolean("Working", this.working);
        return super.writeToNBT(compound);
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        this.extensionProgress = data.getInteger("Extension");
        this.pushing = data.getBoolean("Pushing");
        this.working = data.getBoolean("Working");
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setInteger("Extension", this.extensionProgress);
        data.setBoolean("Pushing", this.pushing);
        data.setBoolean("Working", this.working);
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
