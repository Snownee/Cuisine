package snownee.cuisine.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.tiles.TileWok;

public class ContainerNameFood extends Container
{
    public static class SlotFood extends Slot
    {

        public SlotFood(IInventory inventoryIn, int index)
        {
            super(inventoryIn, index, 0, 0);
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return false;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn)
        {
            return false;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public boolean isEnabled()
        {
            return false;
        }

    }

    public static class InventoryFood extends InventoryBasic
    {

        public InventoryFood()
        {
            super("", false, 1);
        }

        @Override
        public boolean isUsableByPlayer(EntityPlayer player)
        {
            return false;
        }

    }

    private final TileWok tile;
    private final InventoryFood inventory = new InventoryFood();
    private final SlotFood slot;

    public ContainerNameFood(TileWok tile)
    {
        this.tile = tile;
        addSlotToContainer(this.slot = new SlotFood(inventory, 0));
        if (!tile.getWorld().isRemote)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                inventory.setInventorySlotContents(0, tile.serveDishAndReset());
            });
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!playerIn.world.isRemote)
        {
            this.clearContainer(playerIn, playerIn.world, inventory);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

}
