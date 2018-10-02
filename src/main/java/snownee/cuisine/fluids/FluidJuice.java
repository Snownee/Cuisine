package snownee.cuisine.fluids;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.Validate;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.cuisine.util.I18nUtil;

public class FluidJuice extends VaporizableFluid
{

    public FluidJuice(String name)
    {
        super(name, new ResourceLocation(Cuisine.MODID, "block/" + name + "_still"), new ResourceLocation(Cuisine.MODID, "block/" + name + "_flow"));
    }

    @Override
    public int getColor(FluidStack stack)
    {
        return stack.tag == null ? 0 : mixColor(deserialize(stack.tag));
    }

    public static void addIngredient(FluidStack fluid, WeightedMaterial newMaterial)
    {
        List<WeightedMaterial> materials = deserialize(fluid.tag == null ? new NBTTagCompound() : fluid.tag);
        boolean flag = true;
        for (WeightedMaterial material : materials)
        {
            if (material.material == newMaterial.material)
            {
                material.weight += newMaterial.weight;
                flag = false;
                break;
            }
        }
        if (flag)
        {
            materials.add(newMaterial);
        }
        fluid.amount += newMaterial.weight;
        fluid.tag = serialize(materials);
    }

    public static void scaleIngredients(List<WeightedMaterial> materials)
    {
        // TODO
    }

    public static int mixColor(List<WeightedMaterial> materials)
    {
        float size = 0;
        float r = 0;
        float g = 0;
        float b = 0;
        for (WeightedMaterial material : materials)
        {
            int color = material.material.getRawColorCode();
            r += material.weight * (color >> 16 & 255) / 255.0F;
            g += material.weight * (color >> 8 & 255) / 255.0F;
            b += material.weight * (color >> 0 & 255) / 255.0F;
            size += material.weight;
        }
        if (size > 0)
        {
            r = r / size * 255.0F;
            g = g / size * 255.0F;
            b = b / size * 255.0F;
            return (int) r << 16 | (int) g << 8 | (int) b;
        }
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getLocalizedName(FluidStack stack)
    {
        if (stack.tag == null)
        {
            return super.getLocalizedName(stack);
        }
        List<WeightedMaterial> materials = deserialize(stack.tag);
        if (materials == null || materials.isEmpty())
        {
            return super.getLocalizedName(stack);
        }
        if (materials.size() == 1)
        {
            WeightedMaterial weightedMaterial = materials.get(0);
            Ingredient ingredient = new Ingredient(weightedMaterial.material, Form.JUICE, weightedMaterial.weight);
            return ingredient.getTranslation();
        }
        else
        {
            MaterialCategory category = null;
            EnumSet<MaterialCategory> sampleCats = EnumSet.of(MaterialCategory.FRUIT, MaterialCategory.VEGETABLES);
            for (WeightedMaterial material : materials)
            {
                for (MaterialCategory cat : sampleCats)
                {
                    if (cat != category && material.material.isUnderCategoryOf(cat))
                    {
                        if (category == null)
                        {
                            category = cat;
                        }
                        else
                        {
                            return I18nUtil.translate("fluid.juice.mixed");
                        }
                    }
                }
            }
            return I18nUtil.translate("fluid.juice." + (category == null ? "mixed" : category.toString().toLowerCase(Locale.ENGLISH)));
        }
    }

    static NBTTagCompound serialize(List<WeightedMaterial> materials)
    {
        NBTTagList list = new NBTTagList();
        for (WeightedMaterial material : materials)
        {
            list.appendTag(material.serializeNBT());
        }
        NBTTagCompound data = new NBTTagCompound();
        data.setTag(CuisineSharedSecrets.KEY_INGREDIENT_LIST, list);
        return data;
    }

    static List<WeightedMaterial> deserialize(NBTTagCompound data)
    {
        List<WeightedMaterial> materials = new ArrayList<>(8);
        NBTTagList list = data.getTagList(CuisineSharedSecrets.KEY_INGREDIENT_LIST, Constants.NBT.TAG_COMPOUND);
        for (NBTBase baseTag : list)
        {
            if (baseTag.getId() == Constants.NBT.TAG_COMPOUND)
            {
                Validate.isTrue(baseTag instanceof NBTTagCompound);
                WeightedMaterial material = new WeightedMaterial(null, 0);
                material.deserializeNBT((NBTTagCompound) baseTag);
                materials.add(material);
            }
        }
        return materials;
    }

    public static class WeightedMaterial implements INBTSerializable<NBTTagCompound>
    {
        Material material;
        float weight;

        public WeightedMaterial(Material material, float weight)
        {
            this.material = material;
            this.weight = weight;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound data = new NBTTagCompound();
            data.setString(CuisineSharedSecrets.KEY_MATERIAL, material.getID());
            data.setFloat("weight", weight);
            return data;
        }

        @Override
        public void deserializeNBT(NBTTagCompound data)
        {
            final String materialKey = data.getString(CuisineSharedSecrets.KEY_MATERIAL);
            material = CulinaryHub.API_INSTANCE.findMaterial(materialKey);
            if (material == null)
            {
                throw new NullPointerException(String.format("Unknown material '%s'", materialKey));
            }
            weight = data.getFloat("weight");
        }
    }
}
