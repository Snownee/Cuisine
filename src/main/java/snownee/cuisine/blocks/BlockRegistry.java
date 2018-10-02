package snownee.cuisine.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;

@Mod.EventBusSubscriber(modid = Cuisine.MODID)
public final class BlockRegistry // Named so due to legacy reason
{

    @SubscribeEvent
    public static void onCropsGrowPost(BlockEvent.CropGrowEvent event)
    {
        IBlockState state = event.getState();
        if (state.getBlock() instanceof BlockCorn && ((BlockCorn) state.getBlock()).getAge(state, event.getWorld(), event.getPos()) > 1)
        {
            event.getWorld().setBlockState(event.getPos().up(), ((BlockCorn) state.getBlock()).withAge(8));
        }
    }

    @SubscribeEvent
    public static void onChoppingBoardClick(PlayerInteractEvent.LeftClickBlock event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        if (!event.getEntityPlayer().isCreative() || state.getBlock() != CuisineRegistry.CHOPPING_BOARD)
        {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (stack.getItem() == CuisineRegistry.KITCHEN_KNIFE || stack.getItem().getToolClasses(stack).contains("axe"))
        {
            event.setCanceled(true);
            state.getBlock().onBlockClicked(world, pos, event.getEntityPlayer());
        }
    }

}
