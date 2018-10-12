package snownee.cuisine.plugins;

import net.minecraft.block.state.IBlockState;
import snownee.cuisine.Cuisine;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.tiles.TileBasinHeatable;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.definition.ItemDefinition;
import toughasnails.api.TANBlocks;
import toughasnails.api.item.TANItems;

@KiwiModule(modid = Cuisine.MODID, name = "toughasnails", dependency = "toughasnails", optional = true)
public class TANCompat implements IModule
{
    @Override
    public void init()
    {
        Drink.Builder.FEATURE_INPUTS.put(ItemDefinition.of(TANItems.ice_cube), Drink.DrinkType.SMOOTHIE);

        for (int i = 0; i < 8; i++)
        {
            @SuppressWarnings("deprecation")
            IBlockState state = TANBlocks.campfire.getStateFromMeta(i << 1 | 1);
            TileBasinHeatable.STATE_HEAT_SOURCES.put(state, i / 4 + 2);
        }

        @SuppressWarnings("deprecation")
        IBlockState state = TANBlocks.temperature_coil.getStateFromMeta(9);
        TileBasinHeatable.STATE_HEAT_SOURCES.put(state, 4);
    }
}
