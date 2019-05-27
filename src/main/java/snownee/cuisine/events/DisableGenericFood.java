package snownee.cuisine.events;

import net.minecraft.block.BlockCake;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemFood;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DisableGenericFood
{
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        if (event.getItemStack().getItem() instanceof ItemFood)
        {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        if (state.getBlock() instanceof BlockCake)
        {
            event.setCanceled(true);
        }
    }
}
