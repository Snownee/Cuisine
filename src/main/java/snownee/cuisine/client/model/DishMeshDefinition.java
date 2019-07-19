package snownee.cuisine.client.model;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.client.CuisineItemRendering;
import snownee.cuisine.internal.CuisineSharedSecrets;

public final class DishMeshDefinition implements ItemMeshDefinition
{

    public static final DishMeshDefinition INSTANCE = new DishMeshDefinition();

    private DishMeshDefinition()
    {
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack)
    {
        if (stack.getTagCompound() == null)
        {
            return new ModelResourceLocation(CuisineItemRendering.EMPTY_MODEL, "inventory");
        }
        final String type = stack.getTagCompound().getString(CuisineSharedSecrets.KEY_TYPE);
        CompositeFood food;
        if (!type.isEmpty() && (food = CulinaryHub.API_INSTANCE.deserialize(new ResourceLocation(type), stack.getTagCompound())) != null)
        {
            return new ModelResourceLocation(new ResourceLocation(Cuisine.MODID, "dish/" + food.getOrComputeModelType()), "inventory");
        }
        else if (stack.hasTagCompound() && stack.getTagCompound().hasKey("model", Constants.NBT.TAG_STRING))
        {
            return new ModelResourceLocation(new ResourceLocation(Cuisine.MODID, "dish/" + stack.getTagCompound().getString("model")), "inventory");
        }
        else
        {
            return new ModelResourceLocation(CuisineItemRendering.EMPTY_MODEL, "inventory");
        }
    }
}
