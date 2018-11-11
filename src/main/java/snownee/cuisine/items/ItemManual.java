package snownee.cuisine.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import snownee.cuisine.Cuisine;
import snownee.kiwi.item.ItemMod;

public class ItemManual extends ItemMod
{

    public ItemManual(String name)
    {
        super(name);
        setMaxStackSize(1);
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack heldThing = player.getHeldItem(hand);
        /*player.openGui(Cuisine.getInstance(), CuisineGUI.MANUAL, world, hand == EnumHand.MAIN_HAND
                ? player.inventory.currentItem
                : player.inventory.mainInventory.size() + player.inventory.armorInventory.size(), 0, 0);*/
        return new ActionResult<>(EnumActionResult.SUCCESS, heldThing);
    }

}
