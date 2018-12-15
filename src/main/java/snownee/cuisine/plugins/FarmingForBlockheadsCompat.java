package snownee.cuisine.plugins;

import java.util.Locale;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.items.ItemBasicFood.Variants.SubItem;
import snownee.cuisine.items.ItemCrops;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.VariantsHolder.Variant;

@KiwiModule(modid = Cuisine.MODID, name = "farmingforblockheads", dependency = "farmingforblockheads", optional = true)
public class FarmingForBlockheadsCompat implements IModule
{
    @Override
    public void init()
    {
        for (Variant<? extends SubItem> variant : CuisineRegistry.CROPS.getVariants())
        {
            addTrade(variant == ItemCrops.Variants.BAMBOO_SHOOT ? MarketCategory.SAPLINGS : MarketCategory.SEEDS, new ItemStack(CuisineRegistry.CROPS, 1, variant.getMeta()));
        }
        NonNullList<ItemStack> stacks = NonNullList.create();
        CuisineRegistry.SAPLING.getSubBlocks(Cuisine.CREATIVE_TAB, stacks);
        for (ItemStack stack : stacks)
        {
            addTrade(MarketCategory.SAPLINGS, stack);
        }
    }

    private static void addTrade(MarketCategory category, ItemStack stack)
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setTag("OutputItem", stack.serializeNBT());
        data.setTag("CostItem", new ItemStack(Items.EMERALD).serializeNBT());
        data.setString("Category", category.toString());
        FMLInterModComms.sendMessage("farmingforblockheads", "RegisterMarketEntry", data);
    }

    private enum MarketCategory
    {
        SEEDS, SAPLINGS, OTHER;

        @Override
        public String toString()
        {
            return "farmingforblockheads:" + super.toString().toLowerCase(Locale.ROOT);
        }
    }
}
