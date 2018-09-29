package snownee.cuisine.events;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
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
            ArrayList<IRecipe> recipes = Lists.newArrayList(CraftingManager.REGISTRY);
            recipes.removeIf(recipe -> !recipe.getRegistryName().getNamespace().equals(Cuisine.MODID) || recipe.getRecipeOutput().isEmpty());
            event.player.unlockRecipes(recipes);
        }
    }
}
