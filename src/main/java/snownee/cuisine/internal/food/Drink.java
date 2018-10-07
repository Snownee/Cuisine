package snownee.cuisine.internal.food;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Seasoning;

public class Drink extends CompositeFood
{
    public static final class Builder extends CompositeFood.Builder<Drink>
    {
        private Drink completed;

        protected Builder(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects)
        {
            super(ingredients, seasonings, effects);
        }

        @Override
        public Class<Drink> getType()
        {
            return Drink.class;
        }

        @Override
        public Optional<Drink> build(CookingVessel vessel, EntityPlayer cook)
        {
            return Optional.empty();
        }
    }

    public static final ResourceLocation DISH_ID = new ResourceLocation(Cuisine.MODID, "drink");

    private String modelType;

    protected Drink(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects)
    {
        super(ingredients, seasonings, effects);
    }

    @Override
    public ResourceLocation getIdentifier()
    {
        return DISH_ID;
    }

    @Override
    public ItemStack getBaseItem()
    {
        return new ItemStack(CuisineRegistry.DRINK);
    }

    @Override
    public String getOrComputeModelType()
    {
        return modelType != null ? modelType : "bottle";
    }

    @Override
    public void setModelType(String type)
    {
        this.modelType = type;
    }

    @Override
    public Collection<String> getKeywords()
    {
        return null;
    }

}
