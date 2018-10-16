package snownee.cuisine.util;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public class BaublesWrapper
{
    public static int isBaubleEquipped(EntityPlayer player, Item item)
    {
        return BaublesApi.isBaubleEquipped(player, item);
    }
}
