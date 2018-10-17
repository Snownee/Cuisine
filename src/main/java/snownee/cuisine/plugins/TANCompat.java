package snownee.cuisine.plugins;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.food.Drink.DrinkType;
import snownee.cuisine.tiles.TileBasinHeatable;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.definition.ItemDefinition;
import toughasnails.api.TANBlocks;
import toughasnails.api.TANCapabilities;
import toughasnails.api.TANPotions;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.item.TANItems;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.temperature.Temperature;

@KiwiModule(modid = Cuisine.MODID, name = "toughasnails", dependency = "toughasnails", optional = true)
public class TANCompat implements IModule
{
    public static Potion heat_resistance = TANPotions.heat_resistance;

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
        if (event.getEntityLiving() instanceof EntityPlayer && event.getItem().hasCapability(CulinaryCapabilities.FOOD_CONTAINER, null))
        {
            FoodContainer container = event.getItem().getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
            CompositeFood food = container.get(); // Null-safety is guaranteed by the hasCapability check
            if (food != null && food.getKeywords().contains("drink"))
            {
                if (enableThirst() && event.getEntityLiving().hasCapability(TANCapabilities.THIRST, null))
                {
                    IThirst handler = event.getEntityLiving().getCapability(TANCapabilities.THIRST, null);
                    handler.setExhaustion(0);
                    handler.addStats(food.getFoodLevel() * 2, food.getSaturationModifier());
                }
                if (enableTemperature() && event.getEntityLiving().hasCapability(TANCapabilities.TEMPERATURE, null))
                {
                    if (food.getClass() == Drink.class && ((Drink) food).getDrinkType() == DrinkType.SMOOTHIE)
                    {
                        ITemperature handler = event.getEntityLiving().getCapability(TANCapabilities.TEMPERATURE, null);
                        handler.addTemperature(new Temperature(-4));
                        if (TANPotions.heat_resistance != null)
                        {
                            event.getEntityLiving().addPotionEffect(new PotionEffect(TANPotions.heat_resistance, 300));
                        }
                    }
                }
            }
        }
    }

    public static boolean enableTemperature()
    {
        return SyncedConfig.getBooleanValue(GameplayOption.ENABLE_TEMPERATURE);
    }

    public static boolean enableThirst()
    {
        return SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST);
    }

    public static boolean isPlayerThirsty(EntityPlayer player)
    {
        IThirst thirst = player.getCapability(TANCapabilities.THIRST, null);
        if (thirst == null)
        {
            return false;
        }
        return thirst.getThirst() < 20;
    }
}
