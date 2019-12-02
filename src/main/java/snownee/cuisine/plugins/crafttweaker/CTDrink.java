package snownee.cuisine.plugins.crafttweaker;

import java.util.Optional;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import snownee.cuisine.internal.food.Drink;
import snownee.cuisine.internal.food.Drink.DrinkType;
import snownee.kiwi.crafting.input.ProcessingInput;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.cuisine.Drink")
@ZenRegister
public final class CTDrink
{

    private CTDrink()
    {
        // No-op, only used for private access
    }

    @ZenMethod
    public static void addSpecialItem(String drinkType, IIngredient ingredient)
    {
        //CTSupport.DELAYED_ACTIONS.add(new Addition(drinkType, CTSupport.fromIngredient(ingredient)));
        new Addition(drinkType, CTSupport.fromIngredient(ingredient)).apply();
    }

    @ZenMethod
    public static void removeAllSpecialItems(String drinkType)
    {
        //CTSupport.DELAYED_ACTIONS.add(new Removal(drinkType));
        new Removal(drinkType).apply();
    }

    @ZenMethod
    public static void removeAllSpecialItems()
    {
        //CTSupport.DELAYED_ACTIONS.add(new BulkRemoval());
        new BulkRemoval().apply();
    }

    @ZenMethod
    public static DrinkType[] getAllDrinkTypes()
    {
        return DrinkType.DRINK_TYPES.keySet().toArray(new DrinkType[0]);
    }

    private static DrinkType get(String name)
    {
        return Optional.ofNullable(DrinkType.get(name)).orElse(DrinkType.NORMAL);
    }

    private static final class Addition implements IAction
    {

        private final String drinkType;
        private final ProcessingInput ingredient;

        private Addition(String drinkType, ProcessingInput ingredient)
        {
            this.drinkType = drinkType;
            this.ingredient = ingredient;
        }

        @Override
        public void apply()
        {
            DrinkType type = get(drinkType);
            if (type == DrinkType.NORMAL)
            {
                throw new NullPointerException("Cannot find drink type or drink type is the default one.");
            }
            else
            {
                Drink.Builder.FEATURE_INPUTS.put(ingredient, type);
            }
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

    private static final class Removal implements IAction
    {

        private final String drinkType;

        private Removal(String drinkType)
        {
            this.drinkType = drinkType;
        }

        @Override
        public void apply()
        {
            DrinkType type = get(drinkType);
            Drink.Builder.FEATURE_INPUTS.values().removeIf(type::equals);
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

    private static final class BulkRemoval implements IAction
    {
        @Override
        public void apply()
        {
            Drink.Builder.FEATURE_INPUTS.clear();
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

}
