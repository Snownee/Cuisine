package snownee.cuisine.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class StacksUtil
{

    public static void dropInventoryItems(World worldIn, BlockPos pos, IItemHandler inventory, boolean pickupDelay)
    {
        dropInventoryItems(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, inventory, pickupDelay);
    }

    public static void dropInventoryItems(World worldIn, Entity entityAt, IItemHandler inventory, boolean pickupDelay)
    {
        dropInventoryItems(worldIn, entityAt.posX, entityAt.posY, entityAt.posZ, inventory, pickupDelay);
    }

    public static void dropInventoryItems(World worldIn, double x, double y, double z, IItemHandler inventory, boolean pickupDelay)
    {
        if (worldIn.isRemote)
        {
            return;
        }
        for (int i = 0; i < inventory.getSlots(); ++i)
        {
            ItemStack itemstack = inventory.extractItem(i, Integer.MAX_VALUE, false);

            if (!itemstack.isEmpty())
            {
                spawnItemStack(worldIn, x, y, z, itemstack, pickupDelay);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack, boolean pickupDelay)
    {
        if (!worldIn.isRemote && !stack.isEmpty())
        {
            EntityItem entityitem = new EntityItem(worldIn, x, y, z, stack.splitStack(Items.AIR.getItemStackLimit()));
            entityitem.motionX = 0;
            entityitem.motionZ = 0;
            if (pickupDelay)
            {
                entityitem.setDefaultPickupDelay();
            }
            worldIn.spawnEntity(entityitem);
        }
    }

    public static void spawnItemStack(World worldIn, BlockPos pos, ItemStack stack, boolean pickupDelay)
    {
        spawnItemStack(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack, pickupDelay);
    }
}
