package snownee.cuisine.events;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockCuisineCrops;
import snownee.cuisine.blocks.BlockDoubleCrops;
import snownee.cuisine.items.ItemCrops;
import snownee.kiwi.util.definition.ItemDefinition;

public class BetterHarvest
{
    /**
     * A set of item registry name whose corresponding items will signal the BetterHarvest logic
     * to not cancel the event for sake of preserving special right-clicking logic.
     * <p>
     * Currently, the only element inside this set is the name of Scythe from Tinkers' Construct,
     * which has AOE harvesting logic.
     * </p>
     */
    private static final Set<ResourceLocation> PASSING_ITEMS;

    static
    {
        PASSING_ITEMS = Collections.singleton(new ResourceLocation("tconstruct", "scythe"));
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EntityPlayer player = event.getEntityPlayer();
        if (!world.isBlockModifiable(player, pos))
        {
            return;
        }
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        boolean isCrop = block instanceof BlockCrops && ((BlockCrops) block).isMaxAge(state);
        // Equivalent to if (!isCrop) isCrop = ...;
        // However - isCrop |= ... is NOT equivalent to the following. Specifically, |= does not have the short-circuiting
        // logic as of the &&, ||, etc.
        // Do not rewrite to |=.
        isCrop = isCrop || block instanceof BlockCuisineCrops && ((BlockCuisineCrops) block).isMaxAge(state, world, pos);
        if (isCrop)
        {
            if (Arrays.asList(CuisineConfig.GENERAL.betterHarvestBlacklist).contains(block.getRegistryName().toString()))
            {
                return;
            }
            if (!world.isRemote)
            {
                if (block instanceof BlockDoubleCrops && ((BlockDoubleCrops) block).isUpper(state))
                {
                    pos = pos.down();
                    state = world.getBlockState(pos);
                    block = state.getBlock();
                }
                NonNullList<ItemStack> drops = NonNullList.create();
                block.getDrops(drops, world, pos, state, 0);
                world.setBlockToAir(pos);
                boolean fakeplayer = player instanceof FakePlayer && player.getGameProfile().getName().equals("[IF]"); // IF Plant Interactor
                ItemStack drop;
                ItemStack fallbackSeed = ItemStack.EMPTY;
                ItemStack seed = ItemStack.EMPTY;
                if (block instanceof BlockCrops && block == Blocks.WHEAT)
                {
                    fallbackSeed = block.getItem(world, pos, state);
                }
                for (ItemStack dropCandidate : drops) {
                    drop = dropCandidate;
                    if (!player.canPlayerEdit(pos, event.getFace(), drop))
                    {
                        continue;
                    }
                    if (drop.getItem() instanceof IPlantable)
                    {
                        if (block instanceof BlockCrops && drop.isItemEqual(block.getItem(world, pos, state)))
                        {
                            seed = block.getItem(world, pos, state);
                            break;
                        }
                        else if (block instanceof BlockCuisineCrops && ItemDefinition.of(drop).equals(((BlockCuisineCrops) block).getSeed()))
                        {
                            seed = drop;
                            break;
                        }
                        else
                        {
                            fallbackSeed = drop;
                        }
                    }
                }
                if (seed.isEmpty() && !fallbackSeed.isEmpty())
                {
                    seed = fallbackSeed;
                }
                if (!seed.isEmpty())
                {
                    plant(world, pos, player, seed);
                }
                for (ItemStack stack : drops)
                {
                    if (fakeplayer)
                    {
                        player.addItemStackToInventory(stack);
                    }
                    else
                    {
                        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                    }
                }
            }
            if (!PASSING_ITEMS.contains(player.getHeldItem(EnumHand.MAIN_HAND).getItem().getRegistryName()))
            {
                event.setCancellationResult(EnumActionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    private static void plant(World world, BlockPos pos, EntityPlayer player, ItemStack stack)
    {
        IBlockState state;
        if (stack.getItem() instanceof ItemCrops)
        {
            state = CuisineRegistry.CROPS.getVariants().get(stack.getMetadata()).getValue().getBlock().getDefaultState();
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
