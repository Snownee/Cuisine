package snownee.cuisine.events;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockCuisineCrops;
import snownee.cuisine.items.ItemBasicFood;
import snownee.cuisine.items.ItemCrops;
import snownee.cuisine.world.gen.WorldGenGarden;
import snownee.kiwi.util.definition.ItemDefinition;

@EventBusSubscriber(modid = Cuisine.MODID)
public class VillagerHandler
{
    @SubscribeEvent
    public static void onRegisterVillagers(RegistryEvent.Register<VillagerRegistry.VillagerProfession> event)
    {
        VillagerRegistry.VillagerProfession profession = event.getRegistry().getValue(new ResourceLocation("minecraft:farmer"));
        if (profession == null)
        {
            // Skip this if (for whatever reason) it is null.
            return;
        }
        VillagerRegistry.VillagerCareer career = profession.getCareer(0); // farmer

        // career.addTrade(2, new TradeCrops(ItemDefinition.of(CuisineRegistry.LIFE_ESSENCE), 4));
        career.addTrade(3, new TradeCrops(ItemDefinition.of(CuisineRegistry.BASIC_FOOD, ItemBasicFood.Variants.TOFU.getMeta()), 8));
        career.addTrade(4, new TradeCrops(Arrays.asList(ItemDefinition.of(CuisineRegistry.CROPS, ItemCrops.Variants.CHILI.getMeta()), ItemDefinition.of(CuisineRegistry.CROPS, ItemCrops.Variants.GARLIC.getMeta())), 3));

        List<ItemDefinition> crops = Arrays.stream(WorldGenGarden.PLANT_POOL).filter(b -> b instanceof BlockCuisineCrops).map(b -> (((BlockCuisineCrops) b).getCrop())).collect(Collectors.toList());
        career.addTrade(2, new TradeCrops(crops, -17));
        career.addTrade(3, new TradeCrops(crops, -17));
        career.addTrade(4, new TradeCrops(crops, -17));
    }

    static class TradeCrops implements EntityVillager.ITradeList
    {
        private final List<ItemDefinition> stacks;
        private final int count;

        public TradeCrops(ItemDefinition stack, int count)
        {
            this(Collections.singletonList(stack), count);
        }

        public TradeCrops(List<ItemDefinition> stacks, int count)
        {
            this.stacks = stacks;
            this.count = count;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
        {
            MerchantRecipe recipe;
            if (stacks.size() <= 0)
            {
                return;
            }
            ItemStack stack = stacks.get(random.nextInt(stacks.size())).getItemStack();
            int i = Math.max(Math.abs(count), 3);
            i = i - 2 + random.nextInt(5);
            stack.setCount(i);
            if (count > 0) // 玩家用绿宝石换作物
            {
                recipe = new MerchantRecipe(new ItemStack(Items.EMERALD), stack);
            }
            else // 玩家用作物换绿宝石
            {
                recipe = new MerchantRecipe(stack, Items.EMERALD);
            }
            recipeList.add(recipe);
        }
    }
}
