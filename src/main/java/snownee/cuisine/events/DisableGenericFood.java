package snownee.cuisine.events;

import net.minecraft.block.BlockCake;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemFood;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DisableGenericFood
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        if (event.getItemStack().getItem() instanceof ItemFood)
        {
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.PASS);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        if (state.getBlock() instanceof BlockCake)
        {
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.PASS);
        }
    }
}
