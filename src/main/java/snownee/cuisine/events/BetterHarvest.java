package snownee.cuisine.events;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockCuisineCrops;
import snownee.cuisine.blocks.BlockDoubleCrops;
import snownee.cuisine.items.ItemCrops;
import snownee.cuisine.items.ItemCrops.Variants.SubCrop;

public class BetterHarvest
{

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        boolean flag = block instanceof BlockCrops && ((BlockCrops) block).isMaxAge(state);
        if (!flag)
        {
            flag = block instanceof BlockCuisineCrops && ((BlockCuisineCrops) block).isMaxAge(state, world, pos);
        }
        if (!world.isRemote && flag)
        {
            if (block instanceof BlockDoubleCrops && ((BlockDoubleCrops) block).isUpper(state))
            {
                pos = pos.down();
                state = world.getBlockState(pos);
                block = state.getBlock();
            }
            EntityPlayer player = event.getEntityPlayer();
            NonNullList<ItemStack> drops = NonNullList.create();
            block.getDrops(drops, world, pos, state, 0);
            world.setBlockToAir(pos);
            boolean planted = false;
            ItemStack stack;
            for (int i = 0; i < drops.size(); i++)
            {
                stack = drops.get(i);
                if (!planted && (stack.getItem() instanceof IPlantable) && player.canPlayerEdit(pos, event.getFace(), stack))
                {
                    plant(world, pos, player, stack);
                    planted = true;
                }
                else if (!planted && i + 1 == drops.size() && block instanceof BlockCrops && ((BlockCrops) block).getItem(world, pos, state).getItem() == Items.WHEAT_SEEDS)
                {
                    plant(world, pos, player, new ItemStack(Items.WHEAT_SEEDS));
                }
                if (player instanceof FakePlayer && player.getGameProfile().getName().equals("[IF]")) // IF Plant Interactor
                {
                    player.addItemStackToInventory(stack);
                }
                else
                {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
        if (flag)
        {
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    private static void plant(World world, BlockPos pos, EntityPlayer player, ItemStack stack)
    {
        IBlockState state;
        if (stack.getItem() instanceof ItemCrops)
        {
            state = ((SubCrop) CuisineRegistry.CROPS.getVariants().get(stack.getMetadata()).getValue()).getBlock().getDefaultState();
        }
        else
        {
            state = ((IPlantable) stack.getItem()).getPlant(world, pos);
        }
        if (player instanceof EntityPlayerMP && world.setBlockState(pos, state, 11))
        {
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
        }
        stack.shrink(1);
    }
}
