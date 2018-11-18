package snownee.cuisine.fluids;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.internal.CuisineSharedSecrets;

public class FluidJuice extends VaporizableFluid
{
    private static final int DEFAULT_COLOR = 0xF08A19;

    public FluidJuice(String name)
    {
        super(name);
    }

    public static FluidStack make(Material material, int amount)
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setString(CuisineSharedSecrets.KEY_MATERIAL, material.getID());
        return new FluidStack(CuisineFluids.JUICE, amount, data);
    }

    @Override
    public int getColor(FluidStack stack)
    {
        Material material = CulinaryHub.API_INSTANCE.findMaterial(stack);
        return material == null ? DEFAULT_COLOR : material.getRawColorCode();
    }

    @Override
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
