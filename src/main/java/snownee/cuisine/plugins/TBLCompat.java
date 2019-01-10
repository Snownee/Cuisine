package snownee.cuisine.plugins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.api.process.Chopping;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.food.Drink.DrinkType;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.definition.ItemDefinition;

@KiwiModule(modid = Cuisine.MODID, name = TBLCompat.MODID, dependency = TBLCompat.MODID, optional = true)
public class TBLCompat implements IModule
{
    static final String MODID = "thebetweenlands";

    @Override
    public void init()
    {
        Item gel = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "snap_ball"));
        if (gel != null)
        {
            Drink.Builder.FEATURE_INPUTS.put(ItemDefinition.of(gel), DrinkType.GELO);
        }

        if (CuisineConfig.GENERAL.axeChopping)
        {
            int amount = CuisineConfig.GENERAL.axeChoppingPlanksOutput;
            Item material = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "items_misc"));
            Item log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "log_weedwood"));
            Item planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "weedwood_planks"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "weedwood_planks"), ItemDefinition.of(log, OreDictionary.WILDCARD_VALUE), new ItemStack(planks, amount)));
            }
            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "weedwood"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "weedwood_planks"), ItemDefinition.of(log), new ItemStack(planks, amount)));
            }
            if (material != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "weedwood_stick"), ItemDefinition.of(planks), new ItemStack(material, CuisineConfig.GENERAL.axeChoppingStickOutput, 20)));
            }

            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "log_rubber"));
            planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "rubber_tree_planks"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "rubber_tree_planks"), ItemDefinition.of(log), new ItemStack(planks, amount)));
            }

            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "log_hearthgrove"));
            planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "hearthgrove_planks"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "hearthgrove_planks"), ItemDefinition.of(log, OreDictionary.WILDCARD_VALUE), new ItemStack(planks, amount)));
            }

            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "log_nibbletwig"));
            planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "nibbletwig_planks"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "nibbletwig_planks"), ItemDefinition.of(log, OreDictionary.WILDCARD_VALUE), new ItemStack(planks, amount)));
            }

            log = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "giant_root"));
            planks = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, "giant_root_planks"));
            if (log != null && planks != null)
            {
                Processing.CHOPPING.add(new Chopping(new ResourceLocation(MODID, "giant_root_planks"), ItemDefinition.of(log), new ItemStack(planks, amount)));
            }

        }
    }
}
