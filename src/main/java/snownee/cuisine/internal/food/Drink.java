package snownee.cuisine.internal.food;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Loader;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.Seasoning;
import snownee.cuisine.api.prefab.DefaultConsumedCollector;
import snownee.cuisine.api.prefab.DefaultTypes;
import snownee.cuisine.internal.CuisinePersistenceCenter;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.cuisine.internal.effect.EffectPotions;
import snownee.cuisine.plugins.TANCompat;
import snownee.kiwi.Kiwi;
import snownee.kiwi.crafting.input.ProcessingInput;
import snownee.kiwi.util.definition.ItemDefinition;
import snownee.kiwi.util.definition.OreDictDefinition;

public class Drink extends CompositeFood
{
    public static final class Builder extends CompositeFood.Builder<Drink>
    {
        public Drink completed;
        public DrinkType drinkType;
        public static final Map<ProcessingInput, DrinkType> FEATURE_INPUTS = new HashMap<>(4);

        static
        {
            FEATURE_INPUTS.put(ItemDefinition.of(Items.SNOWBALL), DrinkType.SMOOTHIE);
            FEATURE_INPUTS.put(ItemDefinition.of(Blocks.ICE), DrinkType.SMOOTHIE);
            FEATURE_INPUTS.put(ItemDefinition.of(Blocks.PACKED_ICE), DrinkType.SMOOTHIE);
            FEATURE_INPUTS.put(OreDictDefinition.of("slimeball"), DrinkType.GELO);
            FEATURE_INPUTS.put(OreDictDefinition.of("dustRedstone"), DrinkType.SODA);
        }

        Builder(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects)
        {
            super(ingredients, seasonings, effects);
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

        public static boolean isFeatureItem(ItemStack item)
        {
            return FEATURE_INPUTS.keySet().stream().anyMatch(i -> i.matches(item));
        }

        public static boolean isContainerItem(ItemStack item)
        {
            return FEATURE_INPUTS.values().stream().anyMatch(i -> i.getContainerPre().matches(item));
        }

        public static DrinkType findDrinkType(ItemStack item)
        {
            if (!item.isEmpty())
            {
                for (Map.Entry<ProcessingInput, DrinkType> entry : FEATURE_INPUTS.entrySet())
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
            if (getIngredients().isEmpty())
            {
                return Optional.empty();
            }
            for (Ingredient ingredient : getIngredients())
            {
                ingredient.removeTrait(IngredientTrait.UNDERCOOKED);
            }
            FoodValueCounter counter = new FoodValueCounter(0, 0.4F);
            this.apply(counter, vessel);
            float saturationModifier = counter.getSaturation();
            int foodLevel = counter.getHungerRegen();
            completed = new Drink(getIngredients(), getSeasonings(), getEffects(), foodLevel, saturationModifier, drinkType);
            return Optional.of(completed);
        }

        public static NBTTagCompound toNBT(Drink.Builder builder)
        {
            NBTTagCompound data = new NBTTagCompound();
            NBTTagList ingredientList = new NBTTagList();
            for (Ingredient ingredient : builder.getIngredients())
            {
                ingredientList.appendTag(CuisinePersistenceCenter.serialize(ingredient));
            }
            data.setTag(CuisineSharedSecrets.KEY_INGREDIENT_LIST, ingredientList);

            NBTTagList seasoningList = new NBTTagList();
            for (Seasoning seasoning : builder.getSeasonings())
            {
                seasoningList.appendTag(CuisinePersistenceCenter.serialize(seasoning));
            }
            data.setTag(CuisineSharedSecrets.KEY_SEASONING_LIST, seasoningList);

            NBTTagList effectList = new NBTTagList();
            for (Effect effect : builder.getEffects())
            {
                effectList.appendTag(new NBTTagString(effect.getID()));
            }
            data.setTag(CuisineSharedSecrets.KEY_EFFECT_LIST, effectList);

            data.setString("type", builder.drinkType.getName());

            return data;
        }

        public static Drink.Builder fromNBT(NBTTagCompound data)
        {
            ArrayList<Ingredient> ingredients = new ArrayList<>();
            ArrayList<Seasoning> seasonings = new ArrayList<>();
            ArrayList<Effect> effects = new ArrayList<>();
            NBTTagList ingredientList = data.getTagList(CuisineSharedSecrets.KEY_INGREDIENT_LIST, Constants.NBT.TAG_COMPOUND);
            for (NBTBase baseTag : ingredientList)
            {
                if (baseTag.getId() == Constants.NBT.TAG_COMPOUND)
                {
                    ingredients.add(CuisinePersistenceCenter.deserializeIngredient((NBTTagCompound) baseTag));
                }
            }

            NBTTagList seasoningList = data.getTagList(CuisineSharedSecrets.KEY_SEASONING_LIST, Constants.NBT.TAG_COMPOUND);
            for (NBTBase baseTag : seasoningList)
            {
                if (baseTag.getId() == Constants.NBT.TAG_COMPOUND)
                {
                    seasonings.add(CuisinePersistenceCenter.deserializeSeasoning((NBTTagCompound) baseTag));
                }
            }

            NBTTagList effectList = data.getTagList(CuisineSharedSecrets.KEY_EFFECT_LIST, Constants.NBT.TAG_STRING);
            for (NBTBase baseTag : effectList)
            {
                if (baseTag.getId() == Constants.NBT.TAG_STRING)
                {
                    effects.add(CulinaryHub.API_INSTANCE.findEffect(((NBTTagString) baseTag).getString()));
                }
            }

            Drink.Builder builder = new Drink.Builder(ingredients, seasonings, effects);
            builder.drinkType = DrinkType.DRINK_TYPES.get(data.getString("type"));
            if (builder.drinkType == null)
            {
                builder.drinkType = DrinkType.NORMAL;
            }

            return builder;
        }
    }

    public static class DrinkType
    {
        public static final Map<String, DrinkType> DRINK_TYPES = new HashMap<>(8);

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
            DRINK_TYPES.put(name, this);
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
            if (this == SMOOTHIE)
            {
                Calendar calendar = Calendar.getInstance();
                if (calendar.get(Calendar.MONTH) == Calendar.MAY && calendar.get(Calendar.DAY_OF_MONTH) == 18)
                {
                    return Cuisine.MODID + ".snownee";
                }
            }
            return Cuisine.MODID + "." + name;
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
    private int color = -1;

    protected Drink(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects, int foodLevel, float saturation, DrinkType drinkType)
    {
        super(ingredients, seasonings, effects, foodLevel, saturation, 2);
        this.drinkType = drinkType;

        double size = 0;
        float r = 0;
        float g = 0;
        float b = 0;
        for (Ingredient ingredient : getIngredients())
        {
            int color = ingredient.getMaterial().getRawColorCode();
            r += ingredient.getSize() * (color >> 16 & 255) / 255.0F;
            g += ingredient.getSize() * (color >> 8 & 255) / 255.0F;
            b += ingredient.getSize() * (color & 255) / 255.0F;
            size += ingredient.getSize();
        }
        if (size > 0)
        {
            r = (float) (r / size * 255.0F);
            g = (float) (g / size * 255.0F);
            b = (float) (b / size * 255.0F);
            color = (int) r << 16 | (int) g << 8 | (int) b;
        }
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

    @Override
    public void onEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
        Collection<IngredientBinding> bindings = getEffectBindings();
        EffectCollector collector = new DefaultConsumedCollector();

        // And then apply them
        for (IngredientBinding binding : bindings)
        {
            binding.effect.onEaten(stack, player, this, binding.ingredient, collector);
        }

        // And finally, consume seasonings
        for (Seasoning seasoning : seasonings)
        {
            seasoning.getSpice().onConsumed(stack, player, worldIn, seasoning, collector);
        }

        if (!Loader.isModLoaded("toughasnails") || !Kiwi.isOptionalModuleLoaded(Cuisine.MODID, "toughasnails") || !TANCompat.enableThirst())
        {
            player.getFoodStats().addStats(1, getSaturationModifier());
        }

        double vege = 0, fruit = 0, others = 0;
        for (Ingredient ingredient : getIngredients())
        {
            if (getEffects().stream().anyMatch(e -> e instanceof EffectPotions))
            {
                continue;
            }
            Material material = ingredient.getMaterial();
            if (material.isUnderCategoryOf(MaterialCategory.VEGETABLES))
            {
                vege += ingredient.getSize();
            }
            if (material.isUnderCategoryOf(MaterialCategory.FRUIT))
            {
                fruit += ingredient.getSize();
            }
            if (!material.isUnderCategoryOf(MaterialCategory.VEGETABLES) && !material.isUnderCategoryOf(MaterialCategory.FRUIT))
            {
                others += ingredient.getSize();
            }
        }
        if (vege != 0 || fruit != 0)
        {
            boolean flag;
            if (vege == fruit)
            {
                flag = new Random().nextBoolean();
            }
            else
            {
                flag = vege > fruit;
            }
            int duration = (int) (Math.log(1.5 + (flag ? vege : fruit) * 1.5 + (flag ? fruit : vege) * 0.6 + others * 0.3) * 1000);
            if (getIngredients().size() < 2)
            {
                duration *= 0.8;
            }
            Potion potion = MobEffects.SPEED;
            if (drinkType == DrinkType.NORMAL || drinkType == DrinkType.GELO)
            {
                potion = flag ? MobEffects.JUMP_BOOST : MobEffects.SPEED;
            }
            else if (drinkType == DrinkType.SODA)
            {
                potion = flag ? CuisineRegistry.COLD_BLOOD : MobEffects.STRENGTH;
            }
            else if (drinkType == DrinkType.SMOOTHIE)
            {
                potion = flag ? CuisineRegistry.TOUGHNESS : MobEffects.RESISTANCE;
            }
            collector.addEffect(DefaultTypes.POTION, new PotionEffect(potion, duration, 0, true, true));
        }
        collector.apply(this, player);
        PotionEffect effect = player.getActivePotionEffect(CuisineRegistry.EFFECT_RESISTANCE);
        if (effect != null)
        {
            player.removePotionEffect(CuisineRegistry.EFFECT_RESISTANCE);
            player.addPotionEffect(new PotionEffect(CuisineRegistry.EFFECT_RESISTANCE, (int) (effect.getDuration() * 0.25), effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
        }
    }

    public static NBTTagCompound serialize(Drink drink)
    {
        NBTTagCompound data = new NBTTagCompound();
        NBTTagList ingredientList = new NBTTagList();

        for (Ingredient ingredient : drink.ingredients)
        {
            ingredientList.appendTag(CuisinePersistenceCenter.serialize(ingredient));
        }
        data.setTag(CuisineSharedSecrets.KEY_INGREDIENT_LIST, ingredientList);

        NBTTagList seasoningList = new NBTTagList();
        for (Seasoning seasoning : drink.seasonings)
        {
            seasoningList.appendTag(CuisinePersistenceCenter.serialize(seasoning));
        }
        data.setTag(CuisineSharedSecrets.KEY_SEASONING_LIST, seasoningList);

        NBTTagList effectList = new NBTTagList();
        for (Effect effect : drink.effects)
        {
            effectList.appendTag(new NBTTagString(effect.getID()));
        }
        data.setTag(CuisineSharedSecrets.KEY_EFFECT_LIST, effectList);

        data.setString("type", drink.getDrinkType().getName());
        data.setInteger(CuisineSharedSecrets.KEY_FOOD_LEVEL, drink.getFoodLevel());
        data.setFloat(CuisineSharedSecrets.KEY_SATURATION_MODIFIER, drink.getSaturationModifier());
        data.setInteger(CuisineSharedSecrets.KEY_SERVES, drink.getServes());
        data.setFloat(CuisineSharedSecrets.KEY_USE_DURATION, drink.getUseDurationModifier());
        return data;
    }

    public static Drink deserialize(NBTTagCompound data)
    {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ArrayList<Seasoning> seasonings = new ArrayList<>();
        ArrayList<Effect> effects = new ArrayList<>();
        NBTTagList ingredientList = data.getTagList(CuisineSharedSecrets.KEY_INGREDIENT_LIST, Constants.NBT.TAG_COMPOUND);
        for (NBTBase baseTag : ingredientList)
        {
            if (baseTag.getId() == Constants.NBT.TAG_COMPOUND)
            {
                ingredients.add(CuisinePersistenceCenter.deserializeIngredient((NBTTagCompound) baseTag));
            }
        }

        NBTTagList seasoningList = data.getTagList(CuisineSharedSecrets.KEY_SEASONING_LIST, Constants.NBT.TAG_COMPOUND);
        for (NBTBase baseTag : seasoningList)
        {
            if (baseTag.getId() == Constants.NBT.TAG_COMPOUND)
            {
                seasonings.add(CuisinePersistenceCenter.deserializeSeasoning((NBTTagCompound) baseTag));
            }
        }

        NBTTagList effectList = data.getTagList(CuisineSharedSecrets.KEY_EFFECT_LIST, Constants.NBT.TAG_STRING);
        for (NBTBase baseTag : effectList)
        {
            if (baseTag.getId() == Constants.NBT.TAG_STRING)
            {
                effects.add(CulinaryHub.API_INSTANCE.findEffect(((NBTTagString) baseTag).getString()));
            }
        }

        int serves = 0;
        if (data.hasKey(CuisineSharedSecrets.KEY_SERVES, Constants.NBT.TAG_INT))
        {
            serves = data.getInteger(CuisineSharedSecrets.KEY_SERVES);
        }

        float duration = 1;
        if (data.hasKey(CuisineSharedSecrets.KEY_USE_DURATION, Constants.NBT.TAG_FLOAT))
        {
            duration = data.getFloat(CuisineSharedSecrets.KEY_USE_DURATION);
        }

        int foodLevel = 0;
        if (data.hasKey(CuisineSharedSecrets.KEY_FOOD_LEVEL, Constants.NBT.TAG_INT))
        {
            foodLevel = data.getInteger(CuisineSharedSecrets.KEY_FOOD_LEVEL);
        }

        float saturation = 0;
        if (data.hasKey(CuisineSharedSecrets.KEY_SATURATION_MODIFIER, Constants.NBT.TAG_FLOAT))
        {
            saturation = data.getFloat(CuisineSharedSecrets.KEY_SATURATION_MODIFIER);
        }

        DrinkType drinkType = null;
        if (data.hasKey("type", Constants.NBT.TAG_STRING))
        {
            drinkType = DrinkType.DRINK_TYPES.get(data.getString("type"));
        }
        if (drinkType == null)
        {
            drinkType = DrinkType.NORMAL;
        }

        Drink drink = new Drink(ingredients, seasonings, effects, foodLevel, saturation, drinkType);
        drink.setServes(serves);
        drink.setUseDurationModifier(duration);

        return drink;
    }

    public int getColor()
    {
        return color;
    }
}
