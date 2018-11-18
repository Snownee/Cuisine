package snownee.cuisine.client.model;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.client.CuisineItemRendering;

public final class DishMeshDefinition implements ItemMeshDefinition
{

    public static final DishMeshDefinition INSTANCE = new DishMeshDefinition();

    private DishMeshDefinition()
    {
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        CompositeFood food;
        if (container != null && (food = container.get()) != null)
        {
            return new ModelResourceLocation(new ResourceLocation(Cuisine.MODID, "dish/" + food.getOrComputeModelType()), "inventory");
        }
        else
        {
            return new ModelResourceLocation(CuisineItemRendering.EMPTY_MODEL, "inventory");
        }
    }
}
