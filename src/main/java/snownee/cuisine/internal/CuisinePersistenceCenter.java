package snownee.cuisine.internal;

import java.util.ArrayList;
import java.util.EnumSet;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.Seasoning;
import snownee.cuisine.api.Spice;
import snownee.cuisine.internal.food.Dish;

public interface CuisinePersistenceCenter
{

    static NBTTagCompound serialize(CompositeFood dish)
    {
        NBTTagCompound data = new NBTTagCompound();
        NBTTagList ingredientList = new NBTTagList();

        for (Ingredient ingredient : dish.getIngredients())
        {
            ingredientList.appendTag(serialize(ingredient));
        }
        data.setTag(CuisineSharedSecrets.KEY_INGREDIENT_LIST, ingredientList);

        NBTTagList seasoningList = new NBTTagList();
        for (Seasoning seasoning : dish.getSeasonings())
        {
            seasoningList.appendTag(serialize(seasoning));
        }
        data.setTag(CuisineSharedSecrets.KEY_SEASONING_LIST, seasoningList);

        NBTTagList effectList = new NBTTagList();
        for (Effect effect : dish.getEffects())
        {
            effectList.appendTag(new NBTTagString(effect.getID()));
        }
        data.setTag(CuisineSharedSecrets.KEY_EFFECT_LIST, effectList);

        String modelType = dish.getOrComputeModelType();
        if (modelType != null)
        {
            data.setString("type", modelType);
        }

        data.setInteger(CuisineSharedSecrets.KEY_SERVES, dish.getServes());
        data.setFloat(CuisineSharedSecrets.KEY_USE_DURATION, dish.getUseDurationModifier());
        return data;
    }

    static NBTTagCompound serialize(Ingredient ingredient)
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setString(CuisineSharedSecrets.KEY_MATERIAL, ingredient.getMaterial().getID());
        data.setString(CuisineSharedSecrets.KEY_FORM, ingredient.getForm().name());
        data.setFloat(CuisineSharedSecrets.KEY_QUANTITY, ingredient.getSize());
        data.setIntArray(CuisineSharedSecrets.KEY_TRAITS, ingredient.getAllTraits().stream().mapToInt(Enum::ordinal).toArray());
        NBTTagList effectList = new NBTTagList();
        for (Effect effect : ingredient.getEffects())
        {
            effectList.appendTag(new NBTTagString(effect.getID()));
        }
        data.setTag(CuisineSharedSecrets.KEY_EFFECT_LIST, effectList);
        return data;
    }

    static NBTTagCompound serialize(Seasoning seasoning)
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setString(CuisineSharedSecrets.KEY_SPICE, seasoning.getSpice().getID());
        data.setInteger(CuisineSharedSecrets.KEY_QUANTITY, seasoning.getSize());
        return data;
    }

    static CompositeFood deserialize(@Nonnull NBTTagCompound data)
    {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ArrayList<Seasoning> seasonings = new ArrayList<>();
        ArrayList<Effect> effects = new ArrayList<>();
        int serves = 0;
        float duration = 1;
        NBTTagList ingredientList = data.getTagList(CuisineSharedSecrets.KEY_INGREDIENT_LIST, Constants.NBT.TAG_COMPOUND);
        for (NBTBase baseTag : ingredientList)
        {
            if (baseTag.getId() == Constants.NBT.TAG_COMPOUND)
            {
                Validate.isTrue(baseTag instanceof NBTTagCompound);
                ingredients.add(deserializeIngredient((NBTTagCompound) baseTag));
            }
        }

        NBTTagList seasoningList = data.getTagList(CuisineSharedSecrets.KEY_SEASONING_LIST, Constants.NBT.TAG_COMPOUND);
        for (NBTBase baseTag : seasoningList)
        {
            if (baseTag.getId() == Constants.NBT.TAG_COMPOUND)
            {
                Validate.isTrue(baseTag instanceof NBTTagCompound);
                seasonings.add(deserializeSeasoning((NBTTagCompound) baseTag));
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

        if (data.hasKey(CuisineSharedSecrets.KEY_SERVES, Constants.NBT.TAG_INT))
        {
            serves = data.getInteger(CuisineSharedSecrets.KEY_SERVES);
        }

        if (data.hasKey(CuisineSharedSecrets.KEY_USE_DURATION, Constants.NBT.TAG_FLOAT))
        {
            duration = data.getFloat(CuisineSharedSecrets.KEY_USE_DURATION);
        }

        Dish dish = new Dish(ingredients, seasonings, effects);
        dish.setServes(serves);
        dish.setUseDurationModifier(duration);

        if (data.hasKey("type", Constants.NBT.TAG_STRING))
        {
            dish.setModelType(data.getString("type"));
        }

        return dish;
    }

    static Ingredient deserializeIngredient(@Nonnull NBTTagCompound data)
    {
        final String materialKey = data.getString(CuisineSharedSecrets.KEY_MATERIAL);
        Material material = CulinaryHub.API_INSTANCE.findMaterial(materialKey);
        if (material == null)
        {
            throw new NullPointerException(String.format("Unknown material '%s'", materialKey));
        }
        Form form = Form.valueOf(data.getString(CuisineSharedSecrets.KEY_FORM));
        float quantity = data.getFloat(CuisineSharedSecrets.KEY_QUANTITY);
        EnumSet<IngredientTrait> traits = EnumSet.noneOf(IngredientTrait.class);
        for (int id : data.getIntArray(CuisineSharedSecrets.KEY_TRAITS))
        {
            traits.add(IngredientTrait.VALUES[id]);
        }
        Ingredient result = new Ingredient(material, form, quantity, traits);
        for (NBTBase baseTag : data.getTagList(CuisineSharedSecrets.KEY_EFFECT_LIST, Constants.NBT.TAG_STRING))
        {
            if (baseTag instanceof NBTTagString)
            {
                Effect e = CulinaryHub.API_INSTANCE.findEffect(((NBTTagString) baseTag).getString());
                if (e != null)
                {
                    result.addEffect(e);
                }
            }
        }
        return result;
    }

    static Seasoning deserializeSeasoning(@Nonnull NBTTagCompound data)
    {
        Spice spice = CulinaryHub.API_INSTANCE.findSpice(data.getString(CuisineSharedSecrets.KEY_SPICE));
        if (spice == null)
        {
            throw new IllegalArgumentException();
        }
        int quantity = data.getInteger(CuisineSharedSecrets.KEY_QUANTITY);
        return new Seasoning(spice, quantity);
    }

}
