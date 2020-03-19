package snownee.cuisine.events;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.CuisineConfig;

public class DisableGenericFood
{
    public static Set<String> IDs;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        if (IDs == null)
        {
            IDs = Sets.newHashSet(CuisineConfig.HARDCORE.disableGenericFoodBlacklist);
        }
        Item item = event.getItemStack().getItem();
        if (item instanceof ItemFood)
        {
            String id = item.getRegistryName().toString();
            if (!IDs.contains(id))
            {
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.PASS);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if (IDs == null)
        {
            IDs = Sets.newHashSet(CuisineConfig.HARDCORE.disableGenericFoodBlacklist);
        }
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if (block instanceof BlockCake)
        {
            String id = block.getRegistryName().toString();
            if (!IDs.contains(id))
            {
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.PASS);
            }
        }
    }
}
