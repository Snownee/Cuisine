package snownee.cuisine.internal.food;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Seasoning;
import snownee.kiwi.crafting.input.ProcessingInput;
import snownee.kiwi.util.definition.ItemDefinition;
import snownee.kiwi.util.definition.OreDictDefinition;

public class Drink extends CompositeFood
{
    public static final class Builder extends CompositeFood.Builder<Drink>
    {
        public Drink completed;
        public DrinkType drinkType;
        protected final Map<ProcessingInput, DrinkType> featureInputs = new HashMap<>(4);

        Builder(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects)
        {
            super(ingredients, seasonings, effects);
            featureInputs.put(ItemDefinition.of(Items.SNOWBALL), DrinkType.SMOOTHIE);
            featureInputs.put(ItemDefinition.of(Blocks.ICE), DrinkType.SMOOTHIE);
            featureInputs.put(ItemDefinition.of(Blocks.PACKED_ICE), DrinkType.SMOOTHIE);
            featureInputs.put(OreDictDefinition.of("slimeball"), DrinkType.GELO);
            featureInputs.put(OreDictDefinition.of("dustRedstone"), DrinkType.SODA);
            drinkType = DrinkType.NORMAL;
        }

        private Builder()
        {
            this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        public static Drink.Builder create()
        {
            return new Drink.Builder();
        }

        public boolean isFeatureItem(ItemStack item)
        {
            return featureInputs.keySet().stream().anyMatch(i -> i.matches(item));
        }

        public boolean isContainerItem(ItemStack item)
        {
            return featureInputs.values().stream().anyMatch(i -> i.getContainerPre().matches(item));
        }

        public DrinkType findDrinkType(ItemStack item)
        {
            if (!item.isEmpty())
            {
                for (Map.Entry<ProcessingInput, DrinkType> entry : featureInputs.entrySet())
                {
                    if (entry.getKey().matches(item))
                    {
                        return entry.getValue();
                    }
                }
            }
            return DrinkType.NORMAL;
        }

        @Override
        public Class<Drink> getType()
        {
            return Drink.class;
        }

        @Override
        public double getMaxSize()
        {
            return 2;
        }

        @Override
        public int getMaxIngredientLimit()
        {
            return 4;
        }

        @Override
        public Optional<Drink> build(CookingVessel vessel, EntityPlayer cook)
        {
            if (getIngredients().isEmpty() || getIngredients().size() + getSeasonings().size() < 2)
            {
                return Optional.empty();
            }
            completed = new Drink(getIngredients(), getSeasonings(), getEffects(), drinkType);
            return Optional.of(completed);
        }
    }

    public static class DrinkType
    {
        public static final DrinkType NORMAL = new DrinkType("drink", ItemDefinition.of(Items.GLASS_BOTTLE));
        public static final DrinkType SMOOTHIE = new DrinkType("smoothie", ItemDefinition.of(CuisineRegistry.PLACED_DISH));
        public static final DrinkType GELO = new DrinkType("gelo", ItemDefinition.EMPTY);
        public static final DrinkType SODA = new DrinkType("soda", ItemDefinition.of(Items.GLASS_BOTTLE));

        private final ProcessingInput containerPre;
        private final ItemDefinition containerPost;
        private final String name;

        public DrinkType(String name, ItemDefinition container)
        {
            this(name, container, container);
        }

        public DrinkType(String name, ProcessingInput containerPre, ItemDefinition containerPost)
        {
            this.name = name;
            this.containerPre = containerPre;
            this.containerPost = containerPost;
        }

        public String getName()
        {
            return name;
        }

        public String getTranslationKey()
        {
            return Cuisine.MODID + ".drink." + name;
        }

        public ProcessingInput getContainerPre()
        {
            return containerPre;
        }

        public ItemStack getContainerPost()
        {
            return containerPost.getItemStack();
        }
    }

    public static final ResourceLocation DRINK_ID = new ResourceLocation(Cuisine.MODID, "drink");

    private DrinkType drinkType;

    protected Drink(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects, DrinkType drinkType)
    {
        super(ingredients, seasonings, effects);
        this.drinkType = drinkType;
    }

    @Override
    public ResourceLocation getIdentifier()
    {
        return DRINK_ID;
    }

    @Override
    public ItemStack getBaseItem()
    {
        return new ItemStack(CuisineRegistry.DRINK);
    }

    @Override
    public String getOrComputeModelType()
    {
        return drinkType.getName();
    }

    public DrinkType getDrinkType()
    {
        return drinkType;
    }

    @Override
    public void setModelType(String type)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getKeywords()
    {
        return Collections.singletonList("drink");
    }
}
