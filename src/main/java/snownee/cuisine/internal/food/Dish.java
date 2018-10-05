package snownee.cuisine.internal.food;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.Validate;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.Seasoning;
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

    public static final class Builder extends CompositeFood.Builder<Dish>
    {
        private Builder()
        {
            this(new ArrayList<>(), new ArrayList<>());
        }

        Builder(List<Ingredient> ingredients, List<Seasoning> seasonings)
        {
            super(ingredients, seasonings);
        }

        @Override
        public Class<Dish> getType()
        {
            return Dish.class;
        }

        @Override
        public boolean canAddIntoThis(EntityPlayer cook, Ingredient ingredient, CookingVessel vessel)
        {
            if (SkillUtil.hasPlayerLearnedSkill(cook, CulinaryHub.CommonSkills.BIGGER_SIZE))
            {
                return this.getCurrentSize() + ingredient.getSize() <= this.getMaxSize() && ingredient.getMaterial().canAddInto(this, ingredient);
            }
            else
            {
                return this.getCurrentSize() + ingredient.getSize() < this.getMaxSize() * 0.75;
            }
        }

        @Override
        public Optional<Dish> build()
        {
            return Optional.empty(); // FIXME
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

            // TODO Anything else?

            return data;
        }

        public static Dish.Builder fromNBT(NBTTagCompound data)
        {
            ArrayList<Ingredient> ingredients = new ArrayList<>();
            ArrayList<Seasoning> seasonings = new ArrayList<>();
            NBTTagList ingredientList = data.getTagList(CuisineSharedSecrets.KEY_INGREDIENT_LIST, Constants.NBT.TAG_COMPOUND);
            for (NBTBase baseTag : ingredientList)
            {
                if (baseTag.getId() == Constants.NBT.TAG_COMPOUND)
                {
                    Validate.isTrue(baseTag instanceof NBTTagCompound);
                    ingredients.add(CuisinePersistenceCenter.deserializeIngredient((NBTTagCompound) baseTag));
                }
            }

            NBTTagList seasoningList = data.getTagList(CuisineSharedSecrets.KEY_SEASONING_LIST, Constants.NBT.TAG_COMPOUND);
            for (NBTBase baseTag : seasoningList)
            {
                if (baseTag.getId() == Constants.NBT.TAG_COMPOUND)
                {
                    Validate.isTrue(baseTag instanceof NBTTagCompound);
                    seasonings.add(CuisinePersistenceCenter.deserializeSeasoning((NBTTagCompound) baseTag));
                }
            }

            // TODO Anything else?

            return new Dish.Builder(ingredients, seasonings);
        }
    }
}
