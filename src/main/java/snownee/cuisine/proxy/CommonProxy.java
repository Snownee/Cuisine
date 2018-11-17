package snownee.cuisine.proxy;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

public class CommonProxy
{

    public IAnimationStateMachine loadAnimationStateMachine(ResourceLocation identifier, ImmutableMap<String, ITimeValue> parameters)
    {
        // No operation, animation does not exist on server
        return null;
    }

}
