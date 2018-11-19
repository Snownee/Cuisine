package snownee.cuisine.internal.food;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.Seasoning;
import snownee.cuisine.api.Spice;
import snownee.cuisine.api.prefab.DefaultCookedCollector;
import snownee.cuisine.api.util.SkillUtil;
import snownee.cuisine.internal.CuisinePersistenceCenter;
import snownee.cuisine.internal.CuisineSharedSecrets;

/**
 * Dish represents a specific food preparation which are made from combination of
 * various ingredients and seasonings. Typically, a cookware like wok is capable
 * to produce an instance of this.
 */
public class Dish extends CompositeFood
{
    public static final ResourceLocation DISH_ID = new ResourceLocation(Cuisine.MODID, "dish");

    private String modelType;

    public Dish(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects, int hungerHeal, float saturation)
    {
        super(ingredients, seasonings, effects, hungerHeal, saturation);
    }

    @Override
    public ResourceLocation getIdentifier()
    {
        return DISH_ID;
    }

    @Override
    public Collection<String> getKeywords()
    {
        // TODO Anything... else?
        return Arrays.asList("east-asian", "wok");
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

    public static NBTTagCompound serialize(Dish dish)
    {
        NBTTagCompound data = new NBTTagCompound();
        NBTTagList ingredientList = new NBTTagList();

        for (Ingredient ingredient : dish.ingredients)
        {
            ingredientList.appendTag(CuisinePersistenceCenter.serialize(ingredient));
        }
        data.setTag(CuisineSharedSecrets.KEY_INGREDIENT_LIST, ingredientList);

        NBTTagList seasoningList = new NBTTagList();
        for (Seasoning seasoning : dish.seasonings)
        {
            seasoningList.appendTag(CuisinePersistenceCenter.serialize(seasoning));
        }
        data.setTag(CuisineSharedSecrets.KEY_SEASONING_LIST, seasoningList);

        NBTTagList effectList = new NBTTagList();
        for (Effect effect : dish.effects)
        {
            effectList.appendTag(new NBTTagString(effect.getID()));
        }
        data.setTag(CuisineSharedSecrets.KEY_EFFECT_LIST, effectList);

        String modelType = dish.getOrComputeModelType();
        if (modelType != null)
        {
            data.setString("type", modelType);
        }

        data.setInteger(CuisineSharedSecrets.KEY_FOOD_LEVEL, dish.getFoodLevel());
        data.setFloat(CuisineSharedSecrets.KEY_SATURATION_MODIFIER, dish.getSaturationModifier());
        data.setInteger(CuisineSharedSecrets.KEY_SERVES, dish.getServes());
        data.setFloat(CuisineSharedSecrets.KEY_USE_DURATION, dish.getUseDurationModifier());
        return data;
    }

    public static Dish deserialize(NBTTagCompound data)
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

        Dish dish = new Dish(ingredients, seasonings, effects, foodLevel, saturation);
        dish.setServes(serves);
        dish.setUseDurationModifier(duration);

        if (data.hasKey("type", Constants.NBT.TAG_STRING))
        {
            dish.setModelType(data.getString("type"));
        }

        return dish;
    }

    public static final class Builder extends CompositeFood.Builder<Dish>
    {
        private Dish completed;

        private Builder()
        {
            this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        Builder(List<Ingredient> ingredients, List<Seasoning> seasonings, List<Effect> effects)
        {
            super(ingredients, seasonings, effects);
        }

        @Override
        public Class<Dish> getType()
        {
            return Dish.class;
        }

        @Override
        public boolean canAddIntoThis(EntityPlayer cook, Ingredient ingredient, CookingVessel vessel)
        {
            if (ingredient.getForm() == Form.JUICE)
            {
                return false;
            }
            if (SkillUtil.hasPlayerLearnedSkill(cook, CulinaryHub.CommonSkills.BIGGER_SIZE))
            {
                return this.getCurrentSize() + ingredient.getSize() <= this.getMaxSize() && ingredient.getMaterial().canAddInto(this, ingredient);
            }
            else
            {
                return this.getCurrentSize() + ingredient.getSize() < this.getMaxSize() * 0.75 && ingredient.getMaterial().canAddInto(this, ingredient);
            }
        }

        @Override
        public Optional<Dish> build(final CookingVessel vessel, EntityPlayer cook)
        {
            if (this.getIngredients().isEmpty())
            {
                return Optional.empty();
            }
            else if (this.completed != null)
            {
                return Optional.of(this.completed);
            }
            else
            {
                // Calculate hunger regeneration and saturation modifier
                FoodValueCounter counter = new FoodValueCounter(0, 0.4F);
                this.apply(counter, vessel);
                float saturationModifier = counter.getSaturation();
                int foodLevel = counter.getHungerRegen();

                // Grant player cook skill bonus

                // CulinarySkillPointContainer skill = playerIn.getCapability(CulinaryCapabilities.CULINARY_SKILL, null);
                // double modifier = 1.0;
                // if (skill != null)
                // {
                // modifier *= SkillUtil.getPlayerSkillLevel((EntityPlayerMP) playerIn, CuisineSharedSecrets.KEY_SKILL_WOK);
                // SkillUtil.increaseSkillPoint((EntityPlayerMP) playerIn, 1);
                // }

                // Compute side effects

                EffectCollector collector = new DefaultCookedCollector();

                int seasoningSize = 0;
                int waterSize = 0;
                for (Seasoning seasoning : this.getSeasonings())
                {
                    Spice spice = seasoning.getSpice();
                    spice.onCooked(this, seasoning, vessel, collector);
                    if (spice == CulinaryHub.CommonSpices.WATER)
                    {
                        waterSize += seasoning.getSize();
                    }
                    else if (spice != CulinaryHub.CommonSpices.EDIBLE_OIL && spice != CulinaryHub.CommonSpices.SESAME_OIL)
                    {
                        seasoningSize += seasoning.getSize();
                    }
                }
                boolean isPlain = seasoningSize == 0 || (this.getCurrentSize() / seasoningSize) / (1 + waterSize / 3) > 3;

                for (Ingredient ingredient : this.getIngredients())
                {
                    Material material = ingredient.getMaterial();
                    Set<MaterialCategory> categories = material.getCategories();
                    if (isPlain && !categories.contains(MaterialCategory.SEAFOOD) && !categories.contains(MaterialCategory.FRUIT))
                    {
                        ingredient.addTrait(IngredientTrait.PLAIN);
                    }
                    material.onCooked(this, ingredient, vessel, collector);
                }

                this.completed = new Dish(this.getIngredients(), this.getSeasonings(), this.getEffects(), foodLevel, saturationModifier);
                collector.apply(this.completed, cook); // TODO (3TUSK): See, this is why I say this couples too many responsibilities
                // this.completed.setQualityBonus(modifier);
                this.completed.getOrComputeModelType();
                return Optional.of(completed);
            }
        }

        public static Dish.Builder create()
        {
            return new Dish.Builder();
        }

        public static NBTTagCompound toNBT(Dish.Builder builder)
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

            return data;
        }

        public static Dish.Builder fromNBT(NBTTagCompound data)
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

            return new Dish.Builder(ingredients, seasonings, effects);
        }
    }
}
