package snownee.cuisine.plugins;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.tiles.TileBasinHeatable;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.definition.ItemDefinition;
import toughasnails.api.TANBlocks;
import toughasnails.api.TANCapabilities;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.item.TANItems;
import toughasnails.api.stat.capability.IThirst;

@KiwiModule(modid = Cuisine.MODID, name = "toughasnails", dependency = "toughasnails", optional = true)
public class TANCompat implements IModule
{
    @Override
    public void init()
    {
        MinecraftForge.EVENT_BUS.register(this);

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

    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event)
    {
        if (!SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST))
        {
            // Do nothing if Tough As Nail thirsty is disabled
            return;
        }

        if (event.getEntityLiving() instanceof EntityPlayer && event.getEntityLiving().hasCapability(TANCapabilities.THIRST, null) && event.getItem().hasCapability(CulinaryCapabilities.FOOD_CONTAINER, null))
        {
            FoodContainer container = event.getItem().getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
            CompositeFood food = container.get(); // Null-safety is guaranteed by the hasCapability check
            if (food != null && food.getKeywords().contains("drink"))
            {
                IThirst handler = event.getEntityLiving().getCapability(TANCapabilities.THIRST, null);
                handler.setExhaustion(0);
                handler.addStats(food.getFoodLevel() * 2, food.getSaturationModifier());
            }
        }
    }
}
