package snownee.cuisine.crafting;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.oredict.DyeUtils;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Material;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.cuisine.util.ItemNBTUtil;

public class DrinkBrewingRecipe implements IBrewingRecipe
{
    public static final Set<String> BREWABLE_MATERIALS = new HashSet<>();

    public static void add(Material material)
    {
        if (material.isValidForm(Form.JUICE))
        {
            DrinkBrewingRecipe.BREWABLE_MATERIALS.add(material.getID());
        }
    }

    @Override
    public boolean isInput(ItemStack input)
    {
        if (input.getItem() != CuisineRegistry.BOTTLE)
        {
            return false;
        }
        if (ItemNBTUtil.verifyExistence(input, "potion"))
        {
            return true;
        }
        if (input.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
        {
            IFluidHandlerItem handler = input.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            FluidStack fluid = handler.drain(Integer.MAX_VALUE, false);
            if (fluid.tag != null && fluid.tag.hasKey(CuisineSharedSecrets.KEY_MATERIAL, Constants.NBT.TAG_STRING))
            {
                return BREWABLE_MATERIALS.contains(fluid.tag.getString(CuisineSharedSecrets.KEY_MATERIAL));
            }
        }
        return false;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient)
    {
        return PotionHelper.isTypeConversionReagent(ingredient) || DyeUtils.isDye(ingredient);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        ItemStack dummy = makeDummyPotionItem(input);
        if (PotionHelper.hasTypeConversions(dummy, ingredient))
        {
            ItemStack dummyOutput = PotionHelper.doReaction(ingredient, dummy);
            if (!dummyOutput.isEmpty() && dummyOutput.hasTagCompound() && dummyOutput.getTagCompound().hasKey("Potion", Constants.NBT.TAG_STRING))
            {
                ItemStack output = input.copy();
                ItemNBTUtil.setString(output, "potion", ItemNBTUtil.getString(dummyOutput, "Potion", "empty"));
                return output;
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack makeDummyPotionItem(ItemStack stack)
    {
        if (stack.getItem() == CuisineRegistry.BOTTLE)
        {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("potion", Constants.NBT.TAG_STRING))
            {
                ItemStack dummy = new ItemStack(Items.POTIONITEM);
                ItemNBTUtil.setString(dummy, "Potion", ItemNBTUtil.getString(stack, "potion", "empty"));
                return dummy;
            }
            else
            {
                return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.AWKWARD);
            }
        }
        return ItemStack.EMPTY;
    }
}
