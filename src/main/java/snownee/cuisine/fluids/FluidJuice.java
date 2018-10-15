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
        return material == null ? DEFAULT_COLOR : material.getRawColorCode();
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

}
