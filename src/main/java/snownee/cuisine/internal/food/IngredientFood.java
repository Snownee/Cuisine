package snownee.cuisine.internal.food;

import java.util.Collection;
import java.util.Collections;
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
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Seasoning;

public class IngredientFood extends CompositeFood
{
    public static final ResourceLocation INGREDIENT_ID = new ResourceLocation(Cuisine.MODID, "ingredient");

    public static final class Builder extends CompositeFood.Builder<IngredientFood>
    {

        @Override
        public Class<IngredientFood> getType()
        {
            return IngredientFood.class;
        }

        @Override
        public Optional<IngredientFood> build(CookingVessel vessel, EntityPlayer cook)
        {
            if (getIngredients().isEmpty())
            {
                return Optional.empty();
            }
            FoodValueCounter counter = new FoodValueCounter(0, 0.4F);
            this.apply(counter, vessel);
            float saturationModifier = counter.getSaturation();
            int foodLevel = counter.getHungerRegen();
            IngredientFood completed = new IngredientFood(getIngredients(), getSeasonings(), getEffects(), foodLevel, saturationModifier);
            return Optional.of(completed);
        }

        @Override
        public boolean canAddIntoThis(EntityPlayer cook, Ingredient ingredient, CookingVessel vessel)
        {
            return ingredient.getForm() != Form.JUICE;
        }

        @Override
        public boolean canAddIntoThis(EntityPlayer cook, Seasoning seasoning, CookingVessel vessel)
        {
            return false;
        }

    }

    protected IngredientFood(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects, int hungerHeal, float saturation)
    {
        super(ingredients, seasonings, effects, hungerHeal, saturation, 1);
    }

    @Override
    public ResourceLocation getIdentifier()
    {
        return INGREDIENT_ID;
    }

    @Override
    public Collection<String> getKeywords()
    {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getBaseItem()
    {
        return new ItemStack(CuisineRegistry.INGREDIENT);
    }

    @Override
    public String getOrComputeModelType()
    {
        return "normal";
    }

    @Override
    public void setModelType(String type)
    {
        throw new UnsupportedOperationException();
    }

}
