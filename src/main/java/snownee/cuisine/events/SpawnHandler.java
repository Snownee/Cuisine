package snownee.cuisine.events;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;

public class SpawnHandler
{
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        // TODO Re-evaluate this part
        NBTTagCompound playerData = event.player.getEntityData();
        NBTTagCompound data;
        if (playerData.hasKey(EntityPlayer.PERSISTED_NBT_TAG))
        {
            data = playerData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        }
        else
        {
            data = new NBTTagCompound();
        }

        String key = Cuisine.MODID + ":spawned_book";
        if (!data.hasKey(key) || !data.getBoolean(key))
        {
            ItemHandlerHelper.giveItemToPlayer(event.player, new ItemStack(CuisineRegistry.MANUAL));
            data.setBoolean(key, true);
            playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
        }

        if (event.player instanceof EntityPlayerMP)
        {
            // TODO DO YOU SERIOUSLY WANT TO DO THIS EVERY ITEM PLAYER LOGGED IN?!
            event.player.unlockRecipes(StreamSupport.stream(CraftingManager.REGISTRY.spliterator(), false)
                    .filter(r -> r.getRegistryName().getNamespace().equals(Cuisine.MODID))
                    .collect(Collectors.toList())
            );
        }
    }
}
