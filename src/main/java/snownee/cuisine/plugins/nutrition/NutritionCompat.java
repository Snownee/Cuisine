package snownee.cuisine.plugins.nutrition;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.wescook.nutrition.capabilities.INutrientManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.internal.CuisinePersistenceCenter;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;

@KiwiModule(modid = Cuisine.MODID, name = "nutrition", dependency = "nutrition", optional = true)
public class NutritionCompat implements IModule
{
    private static Capability<INutrientManager> NUTRITION_CAPABILITY;
    public static final Map<MaterialCategory, Nutrient> materialCategoryToNutrient = new HashMap<>();

    static void injectCap(Capability<INutrientManager> capability)
    {
        NUTRITION_CAPABILITY = capability;
    }

    @Override
    public void init()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void refreshData()
    {
        if (materialCategoryToNutrient.isEmpty())
        {
            for (Nutrient nutrient : NutrientList.get())
            {
                switch (nutrient.name)
                {
                case "fruit":
                    materialCategoryToNutrient.put(MaterialCategory.FRUIT, nutrient);
                    break;
                case "grain":
                    materialCategoryToNutrient.put(MaterialCategory.GRAIN, nutrient);
                    break;
                case "vegetable":
                    materialCategoryToNutrient.put(MaterialCategory.VEGETABLES, nutrient);
                    break;
                case "protein":
                    materialCategoryToNutrient.put(MaterialCategory.FISH, nutrient);
                    materialCategoryToNutrient.put(MaterialCategory.SEAFOOD, nutrient);
                    materialCategoryToNutrient.put(MaterialCategory.MEAT, nutrient);
                    materialCategoryToNutrient.put(MaterialCategory.NUT, nutrient);
                    materialCategoryToNutrient.put(MaterialCategory.PROTEIN, nutrient);
                    break;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event)
    {
        Entity entity = event.getEntity();
        if (materialCategoryToNutrient.isEmpty())
        {
            refreshData();
        }
        INutrientManager manager = entity.getCapability(NUTRITION_CAPABILITY, null);
        if (manager == null)
        {
            Cuisine.logger.debug("Entity {} has no INutrientManager. Skip nutrition calculation.", entity);
            return;
        }
        ItemStack stack = event.getItem();
        if (stack.getItem() == CuisineRegistry.INGREDIENT || stack.hasCapability(CulinaryCapabilities.FOOD_CONTAINER, null)) // TODO (Snownee): code reuse
        {
            List<Ingredient> ingredients;
            if (stack.getItem() == CuisineRegistry.INGREDIENT)
            {
                if (stack.getTagCompound() == null)
                {
                    return;
                }
                ingredients = Collections.singletonList(CuisinePersistenceCenter.deserializeIngredient(stack.getTagCompound()));
            }
            else
            {
                FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
                CompositeFood composite;
                if ((composite = container.get()) == null)
                {
                    return;
                }
                ingredients = composite.getIngredients();
            }
            Object2DoubleMap<MaterialCategory> map = new Object2DoubleArrayMap<>();
            for (Ingredient ingredient : ingredients)
            {
                for (MaterialCategory category : ingredient.getMaterial().getCategories())
                {
                    map.put(category, map.getOrDefault(category, 0D) + ingredient.getSize());
                }
            }
            for (Object2DoubleMap.Entry<MaterialCategory> entry : map.object2DoubleEntrySet())
            {
                if (entry.getKey() == MaterialCategory.SUPERNATURAL)
                {
                    manager.add(NutrientList.get(), (float) (entry.getDoubleValue() * 0.1F));
                }
                else if (materialCategoryToNutrient.containsKey(entry.getKey()))
                {
                    manager.add(materialCategoryToNutrient.get(entry.getKey()), (float) (entry.getDoubleValue() * 0.5F));
                }
            }
        }
    }
}
