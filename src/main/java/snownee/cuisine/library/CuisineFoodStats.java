package snownee.cuisine.library;

import java.util.Objects;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;

/*
 * Essentially a hack that we can decrease the amount of hunger and
 * saturation modifier that a basic food item can provide.
 */
public class CuisineFoodStats extends FoodStats
{

    @Override
    public void addStats(ItemFood food, ItemStack item)
    {
        if (CuisineConfig.HARDCORE.enable && CuisineConfig.HARDCORE.lowerFoodLevel && !food.getRegistryName().getNamespace().equals(Cuisine.MODID))
        {
            String foodItemId = Objects.requireNonNull(food.getRegistryName()).toString();
            for (String id : CuisineConfig.HARDCORE.lowerFoodLevelBlacklist)
            {
                if (id.equals(foodItemId))
                {
                    super.addStats(food, item);
                    return;
                }
            }
            double retainRatio = CuisineConfig.HARDCORE.foodLevelRetainRatio;
            this.addStats((int) (Math.max(1, food.getHealAmount(item) * retainRatio)), (float) (food.getSaturationModifier(item) * retainRatio));
        }
        else
        {
            super.addStats(food, item);
        }
    }
}
