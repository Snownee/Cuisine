package snownee.cuisine.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import snownee.cuisine.Cuisine;
import snownee.cuisine.client.gui.CuisineGUI;
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if (!worldIn.isRemote)
        {
            // this.resolveContents(itemstack, playerIn);
        }

        playerIn.openGui(Cuisine.getInstance(), CuisineGUI.MANUAL, worldIn, handIn == EnumHand.MAIN_HAND
                ? playerIn.inventory.currentItem
                : playerIn.inventory.mainInventory.size() + playerIn.inventory.armorInventory.size(), 0, 0);
        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

}
