package snownee.cuisine.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.items.ItemBasicFood.Variants;

@EventBusSubscriber(modid = Cuisine.MODID)
public class ForestBatHandler
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemPickup(EntityItemPickupEvent event)
    {
        ItemStack stack = event.getItem().getItem();
        EntityPlayer player = event.getEntityPlayer();
        if (stack.getItem().getCreatorModId(stack).equals("torcherino"))
        {
            InventoryPlayer inventory = player.inventory;
            for (int i = 0; i < inventory.getSizeInventory(); ++i)
            {
                ItemStack stack2 = inventory.getStackInSlot(i);
                if (stack2.getItem() == CuisineRegistry.BASIC_FOOD && stack2.getMetadata() == Variants.EMPOWERED_CITRON.getMeta())
                {
                    ItemBasicFood.citronSays(player, "torch");
                    event.setCanceled(true);
                    return;
                }
            }
        }
        else if (stack.getItem() == CuisineRegistry.BASIC_FOOD && stack.getMetadata() == Variants.EMPOWERED_CITRON.getMeta())
        {
            ItemBasicFood.citronSays(player, "pickup" + player.world.rand.nextInt(4));
        }
    }
}
