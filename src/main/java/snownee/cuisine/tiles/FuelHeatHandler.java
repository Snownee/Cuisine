package snownee.cuisine.tiles;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.oredict.OreDictionary;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.HeatHandler;
import snownee.kiwi.util.OreUtil;
import snownee.kiwi.util.definition.ItemDefinition;
import snownee.kiwi.util.definition.OreDictDefinition;

public class FuelHeatHandler implements HeatHandler
{
    public static class FuelInfo
    {
        public final int level;
        public final int heat;

        public FuelInfo(int level, int heat)
        {
            this.level = level;
            this.heat = heat;
        }
    }

    public static final Map<ItemDefinition, FuelInfo> ITEM_FUELS = new HashMap<>();
    public static final Map<OreDictDefinition, FuelInfo> ORE_FUELS = new HashMap<>();

    static
    {
        ITEM_FUELS.put(ItemDefinition.of(Items.BLAZE_ROD), new FuelInfo(3, 1000));
        ITEM_FUELS.put(ItemDefinition.of(Items.COAL, OreDictionary.WILDCARD_VALUE), new FuelInfo(3, 800));
        ITEM_FUELS.put(ItemDefinition.of(Blocks.WOOL, OreDictionary.WILDCARD_VALUE), new FuelInfo(1, 100));
        ITEM_FUELS.put(ItemDefinition.of(Blocks.CARPET, OreDictionary.WILDCARD_VALUE), new FuelInfo(1, 67));
        ITEM_FUELS.put(ItemDefinition.of(CuisineRegistry.BAMBOO), new FuelInfo(1, 250));

        ORE_FUELS.put(OreDictDefinition.of("fuelCoke"), new FuelInfo(3, 2000));
        ORE_FUELS.put(OreDictDefinition.of("treeSapling"), new FuelInfo(1, 100));
        ORE_FUELS.put(OreDictDefinition.of("paper"), new FuelInfo(1, 150));
        ORE_FUELS.put(OreDictDefinition.of("sugarcane"), new FuelInfo(1, 100));
    }

    public static FuelInfo registerFuel(ItemDefinition item, int level, int heat)
    {
        if (ITEM_FUELS.containsKey(item))
        {
            return null;
        }
        return ITEM_FUELS.put(item, new FuelInfo(level, heat));
    }

    public static FuelInfo unregisterFuel(ItemDefinition item)
    {
        return ITEM_FUELS.remove(item);
    }

    public static FuelInfo registerFuel(OreDictDefinition ore, int level, int heat)
    {
        if (ORE_FUELS.containsKey(ore))
        {
            return null;
        }
        return ORE_FUELS.put(ore, new FuelInfo(level, heat));
    }

    public static FuelInfo unregisterFuel(OreDictDefinition ore)
    {
        return ORE_FUELS.remove(ore);
    }

    private float heat = 0;

    @Override
    public void update(float bonusRate)
    {
        if (heat > 0)
        {
            heat -= 1 + bonusRate;
            heat = MathHelper.clamp(heat, 0, getMaxHeat());
        }
    }

    @Override
    public float getHeat()
    {
        return heat;
    }

    @Override
    public void setHeat(float heat)
    {
        this.heat = heat;
    }

    public int getLevel()
    {
        if (heat == 0)
        {
            return 0;
        }
        return (int) (heat - 1) / 1000 + 1;
    }

    @Override
    public float getMaxHeat()
    {
        return 3000;
    }

    @Override
    public void addHeat(float delta)
    {
        heat = MathHelper.clamp(heat + delta, 0, getMaxHeat());
    }

    public ItemStack addFuel(ItemStack stack)
    {
        stack = stack.copy();
        FuelInfo info = getFuel(stack);
        if (info != null)
        {
            int max = info.level * 1000;
            if (getHeat() + 20 < max)
            {
                float newHeat = Math.min(heat + info.heat, max);
                setHeat(newHeat);
                stack.shrink(1);
            }
        }
        return stack;
    }

    public static boolean isFuel(ItemStack stack, boolean useVanillaFuels)
    {
        if (stack.isEmpty() || !stack.getItem().getContainerItem(stack).isEmpty())
        {
            return false;
        }
        if (useVanillaFuels && TileEntityFurnace.getItemBurnTime(stack) > 0)
        {
            return true;
        }
        if (ITEM_FUELS.containsKey(ItemDefinition.of(stack)))
        {
            return true;
        }
        if (ITEM_FUELS.containsKey(ItemDefinition.of(stack.getItem(), OreDictionary.WILDCARD_VALUE)))
        {
            return true;
        }
        for (String ore : OreUtil.getOreNames(stack))
        {
            if (ORE_FUELS.containsKey(OreDictDefinition.of(ore)))
            {
                return true;
            }
        }
        return false;
    }

    public static FuelInfo getFuel(ItemStack stack)
    {
        FuelInfo info = ITEM_FUELS.get(ItemDefinition.of(stack));
        if (info == null)
        {
            info = ITEM_FUELS.get(ItemDefinition.of(stack.getItem(), OreDictionary.WILDCARD_VALUE));
        }
        if (info == null)
        {
            for (String ore : OreUtil.getOreNames(stack))
            {
                info = ORE_FUELS.get(OreDictDefinition.of(ore));
                if (info != null)
                {
                    break;
                }
            }
        }
        if (info == null)
        {
            int burnTime = TileEntityFurnace.getItemBurnTime(stack);
            if (burnTime > 0)
            {
                info = new FuelInfo(2, burnTime);
            }
        }
        return info;
    }

}
