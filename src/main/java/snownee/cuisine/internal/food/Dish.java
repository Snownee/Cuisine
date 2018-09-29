package snownee.cuisine.internal.food;

import java.util.List;

import net.minecraft.item.ItemStack;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.Seasoning;

/**
 * Dish is the most common CompositeFood contained by plate
 */
public class Dish extends CompositeFood
{
    private String modelType;

    public Dish()
    {
        super();
    }

    public Dish(List<Ingredient> ingredients)
    {
        super(ingredients);
    }

    public Dish(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects)
    {
        super(ingredients, seasonings, effects);
    }

    @Override
    public String getOrComputeModelType()
    {
        if (this.modelType != null)
        {
            return this.modelType;
        }

        if (ingredients.stream().anyMatch(i -> i.getMaterial().isUnderCategoryOf(MaterialCategory.FISH)))
        {
            this.modelType = "fish0";
        }
        else if (ingredients.stream().anyMatch(i -> i.getMaterial() == CulinaryHub.CommonMaterials.RICE))
        {
            this.modelType = "rice0";
        }
        else if (ingredients.stream().allMatch(i -> i.getMaterial().isUnderCategoryOf(MaterialCategory.MEAT)))
        {
            this.modelType = Math.random() >= 0.5 ? "meat1" : "meat0";
        }
        else if (ingredients.stream().allMatch(i -> i.getMaterial().isUnderCategoryOf(MaterialCategory.VEGETABLES)))
        {
            this.modelType = Math.random() >= 0.5 ? "veges0" : "veges1";
        }
        else
        {
            this.modelType = Math.random() >= 0.5 ? "mixed0" : "mixed1";
        }

        return this.modelType;
    }

    @Override
    public void setModelType(String type)
    {
        this.modelType = type;
    }

    @Override
    public ItemStack getBaseItem()
    {
        return new ItemStack(CuisineRegistry.DISH);
    }

}
