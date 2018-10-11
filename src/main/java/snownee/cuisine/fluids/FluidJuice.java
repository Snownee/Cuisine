package snownee.cuisine.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;

public class FluidJuice extends VaporizableFluid
{
    private static final int DEFAULT_COLOR = 3694022;

    public FluidJuice(String name)
    {
        super(name, new ResourceLocation(Cuisine.MODID, "block/" + name + "_still"), new ResourceLocation(Cuisine.MODID, "block/" + name + "_flow"));
    }

    @Override
    public int getColor(FluidStack stack)
    {
        Material material = CulinaryHub.API_INSTANCE.findMaterial(stack);
        return material == null ? DEFAULT_COLOR : material.getRawColorCode() & 0x00FFFFFF | 0x33000000;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getLocalizedName(FluidStack stack)
    {
        Material material = CulinaryHub.API_INSTANCE.findMaterial(stack);
        if (material == null)
        {
            return super.getLocalizedName(stack);
        }
        Ingredient ingredient = new Ingredient(material, Form.JUICE, stack.amount / 500D);
        return ingredient.getTranslation();
    }

    //        {
    //            double size = 0;
    //            float r = 0;
    //            float g = 0;
    //            float b = 0;
    //            for (WeightedMaterial material : materials)
    //            {
    //                int color = material.material.getRawColorCode();
    //                r += material.weight * (color >> 16 & 255) / 255.0F;
    //                g += material.weight * (color >> 8 & 255) / 255.0F;
    //                b += material.weight * (color & 255) / 255.0F;
    //                size += material.weight;
    //            }
    //            if (size > 0)
    //            {
    //                r = (float) (r / size * 255.0F);
    //                g = (float) (g / size * 255.0F);
    //                b = (float) (b / size * 255.0F);
    //                color = (int) r << 16 | (int) g << 8 | (int) b;
    //            }
    //            else
    //            {
    //                color = DEFAULT_COLOR;
    //            }
    //
    //            if (materials.size() > 1)
    //            {
    //                MaterialCategory category = null;
    //                EnumSet<MaterialCategory> sampleCats = EnumSet.of(MaterialCategory.FRUIT, MaterialCategory.VEGETABLES);
    //                for (WeightedMaterial material : materials)
    //                {
    //                    for (MaterialCategory cat : sampleCats)
    //                    {
    //                        if (cat != category && material.material.isUnderCategoryOf(cat))
    //                        {
    //                            if (category == null)
    //                            {
    //                                category = cat;
    //                            }
    //                            else
    //                            {
    //                                name = "mixed";
    //                                return;
    //                            }
    //                        }
    //                    }
    //                }
    //                name = category == null ? "mixed" : category.toString().toLowerCase(Locale.ENGLISH);
    //            }
    //            else
    //            {
    //                name = null;
    //            }
    //        }
}
