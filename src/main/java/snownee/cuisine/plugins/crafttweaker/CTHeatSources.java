package snownee.cuisine.plugins.crafttweaker;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import snownee.cuisine.tiles.TileBasinHeatable;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.cuisine.HeatSources")
@ZenRegister
public final class CTHeatSources
{

    private CTHeatSources()
    {
        // No-op, only used for private access
    }

    @ZenMethod
    public static void add(int heatValue, IBlockDefinition... blocks)
    {
        CTSupport.DELAYED_ACTIONS.add(new BlockAddition(heatValue, Arrays.asList(blocks).stream().map(CTSupport::toNative).collect(Collectors.toList())));
    }

    @ZenMethod
    public static void add(int heatValue, crafttweaker.api.block.IBlockState... states)
    {
        CTSupport.DELAYED_ACTIONS.add(new BlockStateAddition(heatValue, Arrays.asList(states).stream().map(CTSupport::toNative).collect(Collectors.toList())));
    }

    @ZenMethod
    public static void removeAll()
    {
        CTSupport.DELAYED_ACTIONS.add(new BulkRemoval());
    }

    private static final class BlockAddition implements IAction
    {

        private final int heatValue;
        private final Collection<Block> blocks;

        private BlockAddition(int heatValue, Collection<Block> blocks)
        {
            this.heatValue = heatValue;
            this.blocks = blocks;
        }

        @Override
        public void apply()
        {
            blocks.stream().forEach(block -> {
                TileBasinHeatable.BLOCK_HEAT_SOURCES.put(block, heatValue);
                Item item = Item.getItemFromBlock(block);
                if (item != Items.AIR)
                {
                    ItemStack stack = new ItemStack(item, 1, block.damageDropped(block.getDefaultState()));
                    TileBasinHeatable.BLOCK_TO_ITEM.put(block, stack);
                }
            });
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

    private static final class BlockStateAddition implements IAction
    {

        private final int heatValue;
        private final Collection<IBlockState> states;

        private BlockStateAddition(int heatValue, Collection<IBlockState> states)
        {
            this.heatValue = heatValue;
            this.states = states;
        }

        @Override
        public void apply()
        {
            states.stream().forEach($ -> {
                TileBasinHeatable.STATE_HEAT_SOURCES.put($, heatValue);
                Block block = $.getBlock();
                Item item = Item.getItemFromBlock(block);
                if (item != Items.AIR)
                {
                    ItemStack stack = new ItemStack(item, 1, block.damageDropped($));
                    TileBasinHeatable.STATE_TO_ITEM.put($, stack);
                }
            });
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

    private static final class BulkRemoval implements IAction
    {
        @Override
        public void apply()
        {
            TileBasinHeatable.BLOCK_HEAT_SOURCES.clear();
            TileBasinHeatable.STATE_HEAT_SOURCES.clear();
            TileBasinHeatable.BLOCK_TO_ITEM.clear();
            TileBasinHeatable.STATE_TO_ITEM.clear();
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

}
