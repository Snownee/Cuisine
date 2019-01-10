package snownee.cuisine.events;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.library.RarityManager;

@Deprecated
public class DropHandler
{
    private static final Random RAND = new Random();

    @SubscribeEvent
    public void addEntityDrop(LivingDropsEvent event)
    {
        if (event.getEntity().getEntityWorld().getGameRules().getBoolean("doMobLoot"))
        {
            // do we really let entities drop modified items? that means we need to rewrite
            // furnace recipes and player will face to complex automation
        }
    }

    // modify rarity for vanilla crops
    @SubscribeEvent
    public void addBlockDrop(HarvestDropsEvent event)
    {
        if (event.getWorld().getGameRules().getBoolean("doTileDrops"))
        {
            blockDrop(event, Blocks.CARROTS, Items.CARROT);
            blockDrop(event, Blocks.POTATOES, Items.POTATO);
            blockDrop(event, Blocks.WHEAT, Items.WHEAT);
            blockDrop(event, Blocks.BEETROOTS, Items.BEETROOT);
            blockDrop(event, Blocks.MELON_BLOCK, Items.MELON);
            // we do not modify small mushroom because it can be simply placed and harvested
            blockDrop(event, Blocks.BROWN_MUSHROOM_BLOCK, new ItemBlock(Blocks.BROWN_MUSHROOM));
            blockDrop(event, Blocks.RED_MUSHROOM_BLOCK, new ItemBlock(Blocks.RED_MUSHROOM));
            blockDrop(event, Blocks.CHORUS_PLANT, Items.CHORUS_FRUIT);
            blockDrop(event, Blocks.NETHER_WART, Items.NETHER_WART);
            blockDrop(event, Blocks.PUMPKIN, new ItemBlock(Blocks.PUMPKIN));
            blockDrop(event, Blocks.MELON_BLOCK, Items.MELON);
            blockDrop(event, Blocks.LEAVES, Items.APPLE);
            blockDrop(event, Blocks.LEAVES2, Items.APPLE);
            blockDrop(event, Blocks.COCOA, Items.DYE);
        }
    }

    private static void blockDrop(HarvestDropsEvent event, Block targetBlock, Item targetItem)
    {
        IBlockState state = event.getState();
        if (state.getBlock() == targetBlock)
        {
            if (targetBlock instanceof BlockCrops && !((BlockCrops) state.getBlock()).isMaxAge(state))
            {
                return;
            }
            if (targetBlock instanceof BlockCocoa && state.getValue(BlockCocoa.AGE) < 2)
            {
                return;
            }
            List<ItemStack> drops = event.getDrops();
            for (ItemStack stack : drops)
            {
                if (stack.getItem() == targetItem)
                {
                    // currently 20% chance to get one higher rarity item
                    int chance = 20;
                    if (RAND.nextInt(100) < chance) // nextInt(100) means 0 <= result <= 99
                    {
                        ItemStack newDrop = stack.copy();
                        newDrop.setCount(1);
                        stack.shrink(1);
                        RarityManager.setRarity(newDrop, EnumRarity.UNCOMMON);
                        drops.add(newDrop);
                    }
                    return;
                }
            }
        }
    }
}
