package snownee.cuisine.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import snownee.cuisine.Cuisine;
import snownee.cuisine.client.gui.CuisineGUI;
import snownee.kiwi.item.ItemMod;

import java.util.Objects;

public class ItemManual extends ItemMod
{

    private OpenManualHandler manualHandler;

    public ItemManual(String name)
    {
        super(name);
        this.setMaxStackSize(1);
        this.setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        if (this.manualHandler == null || (hand == EnumHand.OFF_HAND && player.isSneaking()))
        {
            return defaultManualHandler(world, player, hand);
        }
        else
        {
            return this.manualHandler.tryOpenManual(world, player, hand);
        }
    }

    private static ActionResult<ItemStack> defaultManualHandler(World world, EntityPlayer player, EnumHand hand)
    {
        player.openGui(Cuisine.getInstance(), CuisineGUI.MANUAL, world, hand.ordinal(), 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public final ItemManual setOpenManualHandler(OpenManualHandler handler)
    {
        this.manualHandler = Objects.requireNonNull(handler, "Manual handler cannot be null");
        return this;
    }

    public interface OpenManualHandler
    {
        ActionResult<ItemStack> tryOpenManual(World world, EntityPlayer player, EnumHand hand);
    }
}
