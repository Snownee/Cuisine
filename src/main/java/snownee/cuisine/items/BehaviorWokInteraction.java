package snownee.cuisine.items;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import snownee.cuisine.tiles.TileWok;
import snownee.cuisine.util.CuisineFakePlayer;

public class BehaviorWokInteraction extends BehaviorDefaultDispenseItem
{

    @Override
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
    {
        EnumFacing facing = source.getBlockState().getValue(BlockDirectional.FACING);
        BlockPos destination = source.getBlockPos().offset(facing);

        World currentWorld = source.getWorld();

        if (!(currentWorld instanceof WorldServer))
        {
            return stack;
        }

        IBlockState target = currentWorld.getBlockState(destination);
        if (currentWorld.getTileEntity(destination) instanceof TileWok)
        {
            FakePlayer player = CuisineFakePlayer.getInstance((WorldServer) source.getWorld());
            player.setPosition(source.getX(), source.getY(), source.getZ());
            player.setHeldItem(EnumHand.MAIN_HAND, stack);
            PlayerInteractEvent.RightClickBlock evt = new PlayerInteractEvent.RightClickBlock(player, EnumHand.MAIN_HAND, destination, facing.getOpposite(), new Vec3d(destination.getX() + 0.5F, destination.getY() + 0.5F, destination.getZ() + 0.5F));
            if (!MinecraftForge.EVENT_BUS.post(evt))
                target.getBlock().onBlockActivated(source.getWorld(), destination, target, player, EnumHand.MAIN_HAND, facing, 0.5F, 0.5F, 0F);

            player.closeContainer();
            //player.closeScreen();
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
            {
                ItemStack content = player.inventory.getStackInSlot(i);
                if (stack != content && !content.isEmpty())
                {
                    BehaviorDefaultDispenseItem.doDispense(currentWorld, content, 6, facing, BlockDispenser.getDispensePosition(source));
                }
            }
            player.inventory.clear();
            return stack;
        }
        else
        {
            return super.dispenseStack(source, stack);
        }
    }

}
