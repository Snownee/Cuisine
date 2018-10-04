package snownee.cuisine.api.process.prefab;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.MaterialCategory;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.cuisine.fluids.CuisineFluids;
import snownee.cuisine.fluids.FluidDrink;
import snownee.cuisine.internal.CuisinePersistenceCenter;

public class MaterialSqueezing implements BasinInteracting
{
    private final Material material;

    public MaterialSqueezing(Material material)
    {
        if (!(material.isValidForm(Form.JUICE) && (material.isUnderCategoryOf(MaterialCategory.FRUIT) || material.isUnderCategoryOf(MaterialCategory.VEGETABLES))))
        {
            throw new IllegalArgumentException(String.format("material '%s' cannot make juice", material));
        }
        this.material = material;
    }

    @Override
    public boolean matches(ItemStack item, @Nullable FluidStack fluid)
    {
        return matchesItem(item) && (fluid == null || fluid.getFluid() == CuisineFluids.DRINK || CulinaryHub.API_INSTANCE.isKnownMaterial(fluid));
    }

    @Override
    public boolean matchesItem(ItemStack item)
    {
        Ingredient ingredient;
        if (item.getItem() == CuisineRegistry.INGREDIENT)
        {
            NBTTagCompound data = item.getTagCompound();
            if (data == null)
            {
                return false;
            }
            ingredient = CuisinePersistenceCenter.deserializeIngredient(data);
        }
        else
        {
            ingredient = Ingredient.make(item, 0.5F);
        }
        return ingredient != null && ingredient.getMaterial() == material;
    }

    @Override
    public Output getOutputAndConsumeInput(ItemStack item, @Nullable FluidStack fluid)
    {
        Ingredient ingredient;
        if (item.getItem() == CuisineRegistry.INGREDIENT)
        {
            NBTTagCompound data = item.getTagCompound();
            if (data == null)
            {
                return null;
            }
            ingredient = CuisinePersistenceCenter.deserializeIngredient(data);
        }
        else
        {
            ingredient = Ingredient.make(item, 0.5F);
        }
        if (ingredient == null)
        {
            return new Output(null, ItemStack.EMPTY);
        }
        if (fluid != null && fluid.getFluid() != CuisineFluids.DRINK)
        {
            Material material = CulinaryHub.API_INSTANCE.findMaterial(fluid);
            if (material == null)
            {
                return new Output(null, ItemStack.EMPTY);
            }
            FluidDrink.addIngredient(fluid, new FluidDrink.WeightedMaterial(material, fluid.amount));
        }
        FluidStack outputFluid = fluid == null ? new FluidStack(CuisineFluids.DRINK, 0) : fluid.copy();
        FluidDrink.addIngredient(outputFluid, new FluidDrink.WeightedMaterial(ingredient.getMaterial(), ingredient.getSize() * 500));
        item.shrink(1);
        return new Output(outputFluid, ItemStack.EMPTY);
    }

}
