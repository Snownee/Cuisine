package snownee.cuisine.api.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class SpiceBottleContentConsumedEvent extends LivingEvent
{
    private final World world;
    private final ItemStack stack;
    private final Object content;
    private int amount;

    public SpiceBottleContentConsumedEvent(World world, EntityLivingBase entity, ItemStack stack, Object content, int amount)
    {
        super(entity);
        this.world = world;
        this.stack = stack;
        this.content = content;
        this.amount = amount;
    }

    public World getWorld()
    {
        return world;
    }

    public ItemStack getItemStack()
    {
        return stack;
    }

    public Object getContent()
    {
        return content;
    }

    public int getAmount()
    {
        return amount;
    }
}
