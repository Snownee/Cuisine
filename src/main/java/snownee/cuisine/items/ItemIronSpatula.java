package snownee.cuisine.items;

import java.util.ArrayList;
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
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.Seasoning;
import snownee.cuisine.internal.food.Dish;
import snownee.kiwi.item.ItemMod;

public class ItemIronSpatula extends ItemMod implements CookingStrategyProvider
{

    // Adapt CookingStrategyProvider to Capability system is somehow tricky, thus
    // it's not something we should consider right now

    static final class StirFrying implements CookingStrategy
    {

        int oilAmount = 0;
        double currentSize = 0;

        private ArrayList<Ingredient> ingredients = new ArrayList<>();
        private ArrayList<Seasoning> seasonings = new ArrayList<>();
        private ArrayList<Effect> effects = new ArrayList<>();

        @Override
        public void beginCook(CompositeFood dish)
        {
            this.currentSize = dish.getSize();
            this.effects.addAll(dish.getEffects());
        }

        @Override
        public void preCook(Seasoning seasoning, CookingVessel vessel)
        {
            if (seasoning.getSpice() == CulinaryHub.CommonSpices.EDIBLE_OIL)
            {
                this.oilAmount += seasoning.getSize();
            }
            this.seasonings.add(seasoning);
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
            if (vessel.getTemperature() > 100)
            {
                ingredient.removeTrait(IngredientTrait.UNDERCOOKED);
            }

            this.ingredients.add(ingredient);
        }

        @Override
        public void postCook(CookingVessel vessel)
        {
            Collections.shuffle(this.ingredients, Item.itemRand);
        }

        @Override
        public void endCook()
        {

        }

        @Override
        public CompositeFood result()
        {
            return new Dish(this.ingredients, this.seasonings, this.effects);
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
