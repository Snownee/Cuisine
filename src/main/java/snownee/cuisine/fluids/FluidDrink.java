package snownee.cuisine.fluids;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

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

public class FluidDrink extends VaporizableFluid
{
    private static final int DEFAULT_COLOR = 3694022;

    public FluidDrink(String name)
    {
        super(name, new ResourceLocation(Cuisine.MODID, "block/" + name + "_still"), new ResourceLocation(Cuisine.MODID, "block/" + name + "_flow"));
    }

    @Override
    public int getColor(FluidStack stack)
    {
        return (stack.tag == null || !stack.tag.hasKey("color", Constants.NBT.TAG_INT)) ? DEFAULT_COLOR : stack.tag.getInteger("color") | 0x33000000;
    }

    public static void addIngredient(FluidStack fluid, WeightedMaterial newMaterial)
    {
        JuiceInfo info = new JuiceInfo();
        info.deserializeNBT(fluid.tag == null ? new NBTTagCompound() : fluid.tag);
        boolean flag = true;
        for (WeightedMaterial material : info.materials)
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
            info.materials.add(newMaterial);
        }
        info.refreshState();
        fluid.amount += newMaterial.weight;
        fluid.tag = info.serializeNBT();
    }

    public static void scaleIngredients(List<WeightedMaterial> materials)
    {
        // TODO
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getLocalizedName(FluidStack stack)
    {
        if (stack.tag == null)
        {
            return super.getLocalizedName(stack);
        }
        if (stack.tag.hasKey("type", Constants.NBT.TAG_STRING))
        {
            return I18nUtil.translate("fluid.drink." + stack.tag.getString("type"));
        }
        JuiceInfo info = new JuiceInfo();
        info.deserializeNBT(stack.tag);
        if (info.materials != null && info.materials.size() == 1)
        {
            WeightedMaterial weightedMaterial = info.materials.get(0);
            Ingredient ingredient = new Ingredient(weightedMaterial.material, Form.JUICE, weightedMaterial.weight);
            return ingredient.getTranslation();
        }
        return super.getLocalizedName(stack);
    }

    public static class JuiceInfo implements INBTSerializable<NBTTagCompound>
    {
        List<WeightedMaterial> materials;
        @Nullable
        String name;
        int color;

        public List<WeightedMaterial> getMaterials()
        {
            return materials;
        }

        public int getColor()
        {
            return color;
        }

        public void refreshState()
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
                b += material.weight * (color & 255) / 255.0F;
                size += material.weight;
            }
            if (size > 0)
            {
                r = r / size * 255.0F;
                g = g / size * 255.0F;
                b = b / size * 255.0F;
                color = (int) r << 16 | (int) g << 8 | (int) b;
            }
            else
            {
                color = DEFAULT_COLOR;
            }

            if (materials.size() > 1)
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
                                name = "mixed";
                                return;
                            }
                        }
                    }
                }
                name = category == null ? "mixed" : category.toString().toLowerCase(Locale.ENGLISH);
            }
            else
            {
                name = null;
            }
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagList list = new NBTTagList();
            for (WeightedMaterial material : materials)
            {
                list.appendTag(material.serializeNBT());
            }
            NBTTagCompound data = new NBTTagCompound();
            data.setTag(CuisineSharedSecrets.KEY_INGREDIENT_LIST, list);
            if (name != null)
            {
                data.setString("type", name);
            }
            data.setInteger("color", color);
            return data;
        }

        @Override
        public void deserializeNBT(NBTTagCompound data)
        {
            materials = new ArrayList<>(8);
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
            if (data.hasKey("type", Constants.NBT.TAG_STRING))
            {
                name = data.getString("type");
            }
            color = data.getInteger("color");
        }
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
