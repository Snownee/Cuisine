package snownee.cuisine.api.process.prefab;

import java.util.Random;

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
        return matchesItem(item) && (fluid == null || material == CulinaryHub.API_INSTANCE.findMaterial(fluid));
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
    public Output getOutput(ItemStack item, @Nullable FluidStack fluid, Random rand)
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
            return new Output(fluid, ItemStack.EMPTY);
        }
        NBTTagCompound data = new NBTTagCompound();
        data.setString("material", material.getID());
        int amount = fluid == null ? 0 : fluid.amount;
        amount += ingredient.getSize() * 500;
        FluidStack outputFluid = new FluidStack(CuisineFluids.JUICE, amount, data);
        return new Output(outputFluid, ItemStack.EMPTY);
    }

}
