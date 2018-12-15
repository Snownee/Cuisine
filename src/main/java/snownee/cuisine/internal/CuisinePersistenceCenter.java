package snownee.cuisine.internal;

import java.util.EnumSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.Seasoning;
import snownee.cuisine.api.Spice;

public interface CuisinePersistenceCenter
{

    static NBTTagCompound serialize(Ingredient ingredient)
    {
        NBTTagCompound data = new NBTTagCompound();
        if (ingredient.getMaterial() == null)
        {
            return data;
        }
        data.setString(CuisineSharedSecrets.KEY_MATERIAL, ingredient.getMaterial().getID());
        data.setString(CuisineSharedSecrets.KEY_FORM, ingredient.getForm().name());
        data.setDouble(CuisineSharedSecrets.KEY_QUANTITY, ingredient.getSize());
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

    static @Nullable Ingredient deserializeIngredient(@Nonnull NBTTagCompound data)
    {
        if (!data.hasKey(CuisineSharedSecrets.KEY_MATERIAL, Constants.NBT.TAG_STRING))
        {
            return null;
        }
        final String materialKey = data.getString(CuisineSharedSecrets.KEY_MATERIAL);
        Material material = CulinaryHub.API_INSTANCE.findMaterial(materialKey);
        if (material == null)
        {
            // TODO (3TUSK): how about only throwing exceptions in dev environment
            //throw new NullPointerException(String.format("Unknown material '%s'", materialKey));
            return null;
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
