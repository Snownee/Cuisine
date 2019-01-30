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
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
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
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.definition.ItemDefinition;
import snownee.kiwi.util.definition.OreDictDefinition;

public class Drink extends CompositeFood
{
    public static final class Builder extends CompositeFood.Builder<Drink>
    {
        public Drink completed;
        public DrinkType drinkType;
        private int color = -1;
        public static final Map<ProcessingInput, DrinkType> FEATURE_INPUTS = new HashMap<>(4);

        static
        {
            FEATURE_INPUTS.put(ItemDefinition.of(Items.SNOWBALL), DrinkType.SMOOTHIE);
            FEATURE_INPUTS.put(ItemDefinition.of(Blocks.ICE), DrinkType.SMOOTHIE);
            FEATURE_INPUTS.put(ItemDefinition.of(Blocks.PACKED_ICE), DrinkType.SMOOTHIE);
            FEATURE_INPUTS.put(OreDictDefinition.of("slimeball"), DrinkType.GELO);
            FEATURE_INPUTS.put(OreDictDefinition.of("foodGelatine"), DrinkType.GELO);
            FEATURE_INPUTS.put(OreDictDefinition.of("ingotGelatin"), DrinkType.GELO);
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
        public boolean addIngredient(EntityPlayer cook, Ingredient ingredient, CookingVessel vessel)
        {
            boolean flag = super.addIngredient(cook, ingredient, vessel);
            calculateColor();
            return flag;
        }

        @Override
        public boolean removeIngredient(Ingredient ingredient)
        {
            boolean flag = super.removeIngredient(ingredient);
            calculateColor();
            return flag;
        }

        protected void calculateColor()
        {
            int size = 0;
            float r = 0;
            float g = 0;
            float b = 0;
            for (Ingredient ingredient : getIngredients())
            {
                int color = ingredient.getMaterial().getRawColorCode();
                r += (color >> 16 & 255) / 255.0F;
                g += (color >> 8 & 255) / 255.0F;
                b += (color & 255) / 255.0F;
                ++size;
            }
            if (size > 0)
            {
                r = r / size * 255.0F;
                g = g / size * 255.0F;
                b = b / size * 255.0F;
                color = (int) r << 16 | (int) g << 8 | (int) b;
            }
        }

        public int getColor()
        {
            return color | 0xFF000000;
        }

        @Override
        public Class<Drink> getType()
        {
            return Drink.class;
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
            completed = new Drink(getIngredients(), getSeasonings(), getEffects(), foodLevel, saturationModifier, drinkType, color);
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
            data.setInteger("color", builder.getColor());

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
                    Ingredient ingredient = CuisinePersistenceCenter.deserializeIngredient((NBTTagCompound) baseTag);
                    if (ingredient != null)
                    {
                        ingredients.add(ingredient);
                    }
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
            builder.color = data.getInteger("color");

            return builder;
        }
    }

    public static class DrinkType
    {
        public static final Map<String, DrinkType> DRINK_TYPES = new HashMap<>(8);

        public static final DrinkType NORMAL = new DrinkType("drink", ItemDefinition.of(Items.GLASS_BOTTLE), MobEffects.JUMP_BOOST, MobEffects.SPEED);
        public static final DrinkType SMOOTHIE = new DrinkType("smoothie", ItemDefinition.of(CuisineRegistry.PLACED_DISH), CuisineRegistry.TOUGHNESS, MobEffects.RESISTANCE);
        public static final DrinkType GELO = new DrinkType("gelo", ItemDefinition.EMPTY, MobEffects.JUMP_BOOST, MobEffects.SPEED);
        public static final DrinkType SODA = new DrinkType("soda", ItemDefinition.of(Items.GLASS_BOTTLE), CuisineRegistry.COLD_BLOOD, MobEffects.STRENGTH);

        private final ProcessingInput containerPre;
        private final ItemDefinition containerPost;
        private final String name;
        private final Potion potionVege;
        private final Potion potionFruit;

        public DrinkType(String name, ItemDefinition container, Potion potionVege, Potion potionFruit)
        {
            this(name, container, container, potionVege, potionFruit);
            DRINK_TYPES.put(name, this);
        }

        public DrinkType(String name, ProcessingInput containerPre, ItemDefinition containerPost, Potion potionVege, Potion potionFruit)
        {
            this.name = name;
            this.containerPre = containerPre;
            this.containerPost = containerPost;
            this.potionVege = potionVege;
            this.potionFruit = potionFruit;
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

        public Potion getPotionVege()
        {
            return potionVege;
        }

        public Potion getPotionFruit()
        {
            return potionFruit;
        }
    }

    public static final ResourceLocation DRINK_ID = new ResourceLocation(Cuisine.MODID, "drink");

    private DrinkType drinkType;
    private final int color;

    protected Drink(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects, int foodLevel, float saturation, DrinkType drinkType, int color)
    {
        super(ingredients, seasonings, effects, foodLevel, saturation, 2);
        this.drinkType = drinkType;
        this.color = color;
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

    public static final boolean enableThirst()
    {
        return Kiwi.isLoaded(new ResourceLocation(Cuisine.MODID, "toughasnails")) && TANCompat.enableThirst();
    }

    @Override
    public void onEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
        Collection<EffectBinding> bindings = getEffectBindings();
        float modifier = 1;
        for (Seasoning seasoning : seasonings)
        {
            if (seasoning.getSpice() == CulinaryHub.CommonSpices.UNREFINED_SUGAR)
            {
                modifier += seasoning.getSize() * 0.1;
            }
            else if (seasoning.getSpice() == CulinaryHub.CommonSpices.SUGAR)
            {
                modifier += seasoning.getSize() * 0.2;
            }
            else if (seasoning.getSpice() == CulinaryHub.CommonSpices.SALT)
            {
                modifier -= seasoning.getSize() * 0.1;
            }
            else if (seasoning.getSpice() == CulinaryHub.CommonSpices.CRUDE_SALT)
            {
                modifier -= seasoning.getSize() * 0.2;
            }
            else if (seasoning.getSpice() == CulinaryHub.CommonSpices.SOY_SAUCE || seasoning.getSpice() == CulinaryHub.CommonSpices.CHILI_POWDER || seasoning.getSpice() == CulinaryHub.CommonSpices.SICHUAN_PEPPER_POWDER)
            {
                modifier = 0;
                break;
            }
        }
        DefaultConsumedCollector collector = new DefaultConsumedCollector(getFoodLevel(), modifier);

        // And then apply them
        for (EffectBinding binding : bindings)
        {
            binding.effect.onEaten(stack, player, this, binding.ingredients, collector);
        }

        // And finally, consume seasonings
        for (Seasoning seasoning : seasonings)
        {
            seasoning.getSpice().onConsumed(stack, player, worldIn, seasoning, collector);
        }

        if (getFoodLevel() <= 0)
        {
            return;
        }
        if (!enableThirst())
        {
            player.getFoodStats().addStats(1, getSaturationModifier());
        }

        if (!worldIn.isRemote)
        {
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
                    ++vege;
                }
                if (material.isUnderCategoryOf(MaterialCategory.FRUIT))
                {
                    ++fruit;
                }
                if (!material.isUnderCategoryOf(MaterialCategory.VEGETABLES) && !material.isUnderCategoryOf(MaterialCategory.FRUIT))
                {
                    ++others;
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

                Potion potion = flag ? drinkType.getPotionVege() : drinkType.getPotionFruit();
                if (potion == null)
                {
                    potion = MobEffects.SPEED;
                }
                collector.addEffect(DefaultTypes.POTION, new PotionEffect(potion, duration, 0, true, true));
            }
            if (modifier <= 0.1F)
            {
                player.addPotionEffect(new PotionEffect(worldIn.rand.nextBoolean() ? MobEffects.SLOWNESS : MobEffects.MINING_FATIGUE, 1200));
            }
            else if (modifier > 0.25F)
            {
                collector.apply(this, player);
            }
            PotionEffect effect = player.getActivePotionEffect(CuisineRegistry.EFFECT_RESISTANCE);
            if (effect != null)
            {
                player.removePotionEffect(CuisineRegistry.EFFECT_RESISTANCE);
                player.addPotionEffect(new PotionEffect(CuisineRegistry.EFFECT_RESISTANCE, (int) (effect.getDuration() * 0.25), effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
            }
        }
        if (drinkType == DrinkType.SMOOTHIE)
        {
            player.extinguish();
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
        data.setInteger("color", drink.getColor());
        data.setInteger(CuisineSharedSecrets.KEY_FOOD_LEVEL, drink.getFoodLevel());
        data.setFloat(CuisineSharedSecrets.KEY_SATURATION_MODIFIER, drink.getSaturationModifier());
        data.setInteger(CuisineSharedSecrets.KEY_SERVES, drink.getServes());
        data.setFloat(CuisineSharedSecrets.KEY_USE_DURATION, drink.getUseDurationModifier());
        return data;
    }

    public static Drink deserialize(NBTTagCompound data)
    {
        NBTHelper helper = NBTHelper.of(data);
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

        int serves = helper.getInt(CuisineSharedSecrets.KEY_SERVES);
        float duration = helper.getFloat(CuisineSharedSecrets.KEY_USE_DURATION, 1);
        int foodLevel = helper.getInt(CuisineSharedSecrets.KEY_FOOD_LEVEL);
        float saturation = helper.getFloat(CuisineSharedSecrets.KEY_SATURATION_MODIFIER);

        DrinkType drinkType = DrinkType.DRINK_TYPES.get(helper.getString("type", "normal"));
        if (drinkType == null)
        {
            drinkType = DrinkType.NORMAL;
        }

        int color = helper.getInt("color", -1);

        Drink drink = new Drink(ingredients, seasonings, effects, foodLevel, saturation, drinkType, color);
        drink.setServes(serves);
        drink.setUseDurationModifier(duration);

        return drink;
    }

    public int getColor()
    {
        return color;
    }
}
