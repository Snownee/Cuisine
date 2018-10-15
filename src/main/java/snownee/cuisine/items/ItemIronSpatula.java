package snownee.cuisine.items;

import java.util.Collections;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingStrategy;
import snownee.cuisine.api.CookingStrategyProvider;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.Seasoning;
import snownee.kiwi.item.ItemMod;

public class ItemIronSpatula extends ItemMod implements CookingStrategyProvider
{

    // Adapt CookingStrategyProvider to Capability system is somehow tricky, thus
    // it's not something we should consider right now

    static final class StirFrying implements CookingStrategy
    {

        int oilAmount = 0;
        double currentSize = 0;

        @Override
        public void beginCook(CompositeFood.Builder<?> dish)
        {
            this.currentSize = dish.getCurrentSize();
        }

        @Override
        public void preCook(Seasoning seasoning, CookingVessel vessel)
        {
            if (seasoning.getSpice() == CulinaryHub.CommonSpices.EDIBLE_OIL)
            {
                this.oilAmount += seasoning.getSize();
            }
            // TODO (3TUSK): Add other characteristics according to seasoning list
        }

        @Override
        public void cook(Ingredient ingredient, CookingVessel vessel)
        {
            if (oilAmount > 0)
            {
                // Smells good, man!
                ingredient.addTrait(IngredientTrait.AROMATIC);

                // Oily is actually bad, man!
                if (this.oilAmount / this.currentSize > 0.25)
                {
                    ingredient.addTrait(IngredientTrait.OILY);
                }
                else
                {
                    ingredient.removeTrait(IngredientTrait.OILY);
                }
            }

            ingredient.setHeat(ingredient.getHeat() + Item.itemRand.nextInt(10));
            //if (vessel.getTemperature() > 100) // TODO Move this somewhere else? New interface HeatingVessel?
            //{
                ingredient.removeTrait(IngredientTrait.UNDERCOOKED);
            //}

        }

        @Override
        public void postCook(CompositeFood.Builder<?> dish, CookingVessel vessel)
        {
            Collections.shuffle(dish.getIngredients(), Item.itemRand);
        }

        @Override
        public void endCook()
        {

        }

    }

    public ItemIronSpatula(String name)
    {
        super(name);
        setMaxStackSize(1);
        setCreativeTab(Cuisine.CREATIVE_TAB);
    }

    @Nonnull
    @Override
    public CookingStrategy getCookingStrategy(ItemStack stack)
    {
        return new StirFrying();
    }
}
