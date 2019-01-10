package snownee.cuisine.api.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import snownee.cuisine.api.CompositeFood;

import javax.annotation.Nullable;

// TODO (3TUSK): Doc
public abstract class ConsumeCompositeFoodEvent extends Event
{
    private final CompositeFood food;
    private final EntityPlayer consumer;
    private final BlockPos location;

    ConsumeCompositeFoodEvent(CompositeFood food, EntityPlayer consumer, @Nullable BlockPos location)
    {
        this.food = food;
        this.consumer = consumer;
        this.location = location;
    }

    public final CompositeFood getFood()
    {
        return this.food;
    }

    public final EntityPlayer getConsumer()
    {
        return this.consumer;
    }

    public final @Nullable BlockPos getLocation()
    {
        return this.location;
    }

    @Cancelable
    @HasResult
    public static final class Pre extends ConsumeCompositeFoodEvent
    {
        public Pre(CompositeFood food, EntityPlayer consumer, @Nullable BlockPos location)
        {
            super(food, consumer, location);
        }
    }

    public static final class Post extends ConsumeCompositeFoodEvent
    {
        public Post(CompositeFood food, EntityPlayer consumer, @Nullable BlockPos location)
        {
            super(food, consumer, location);
        }
    }
}
