package snownee.cuisine.crafting;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumDyeColor;
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
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.NBTHelper.Tag;

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
        // Why not "Potion":
        // in order not to be caught by VanillaBrewingRecipe, it will drop item NBT
        if (NBTHelper.of(input).hasTag("potion", Tag.STRING))
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
        if (!isInput(input) || !isIngredient(ingredient))
        {
            return ItemStack.EMPTY;
        }
        if (DyeUtils.isDye(ingredient))
        {
            if (!NBTHelper.of(input).hasTag("potion", Tag.STRING))
            {
                return ItemStack.EMPTY;
            }
            ItemStack output = input.copy();
            Optional<EnumDyeColor> result = DyeUtils.colorFromStack(ingredient);
            if (!result.isPresent())
            {
                return ItemStack.EMPTY;
            }
            int color = result.get().getColorValue();
            NBTHelper helper = NBTHelper.of(output);
            if (helper.hasTag("liquidColor", Tag.INT)) // has color already
            {
                int colorBefore = helper.getInt("liquidColor", -1);
                color = Material.mixColor(colorBefore, color, 0.5F);
            }
            helper.setInt("liquidColor", color);
            return output;
        }
        ItemStack dummy = makeDummyPotionItem(input);
        if (PotionHelper.hasTypeConversions(dummy, ingredient))
        {
            ItemStack dummyOutput = PotionHelper.doReaction(ingredient, dummy);
            NBTHelper helper = NBTHelper.of(dummyOutput);
            if (!dummyOutput.isEmpty() && helper.hasTag("Potion", Tag.STRING))
            {
                return NBTHelper.of(input.copy()).setString("potion", helper.getString("Potion", "empty")).getItem();
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack makeDummyPotionItem(ItemStack stack)
    {
        if (stack.getItem() == CuisineRegistry.BOTTLE)
        {
            NBTHelper helper = NBTHelper.of(stack);
            if (helper.hasTag("potion", Tag.STRING))
            {
                ItemStack dummy = new ItemStack(Items.POTIONITEM);
                return NBTHelper.of(dummy).setString("Potion", helper.getString("potion")).getItem();
            }
            else
            {
                return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.AWKWARD);
            }
        }
        return ItemStack.EMPTY;
    }
}
