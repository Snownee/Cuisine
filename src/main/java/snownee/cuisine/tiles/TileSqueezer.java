package snownee.cuisine.tiles;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockDispenser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.animation.TimeValues;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import snownee.cuisine.Cuisine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileSqueezer extends TileBase
{

    private static final ResourceLocation STATE_MACHINE = new ResourceLocation(Cuisine.MODID, "asms/squeezer.json");

    private IAnimationStateMachine stateMachine;

    private TimeValues.VariableValue magic = new TimeValues.VariableValue(0F);

    private boolean triggered;

    public TileSqueezer()
    {
        this.stateMachine = Cuisine.proxy.loadAnimationStateMachine(STATE_MACHINE, ImmutableMap.of("magic", this.magic));
    }

    public void onTriggered(boolean triggered)
    {
        this.triggered = triggered;
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        // TODO The following code is malfunctioning; it cannot properly do the transition from extended to extracting.
        this.triggered = this.world.getBlockState(this.pos).getValue(BlockDispenser.TRIGGERED);
        if (triggered)
        {
            if ("extracted".equals(stateMachine.currentState()))
            {
                magic.setValue(Animation.getWorldTime(this.getWorld()));
                stateMachine.transition("extending");
            }
        }
        else
        {
            if ("extended".equals(stateMachine.currentState()))
            {
                magic.setValue(Animation.getWorldTime(this.getWorld()));
                stateMachine.transition("extracting");
            }
        }
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setBoolean("triggered", this.triggered);
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
