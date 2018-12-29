package snownee.cuisine.plugins.crafttweaker;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.process.CuisineProcessingRecipe;
import snownee.cuisine.api.process.CuisineProcessingRecipeManager;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.crafting.input.ProcessingInput;
import snownee.kiwi.crafting.input.RegularItemStackInput;
import snownee.kiwi.util.definition.OreDictDefinition;

import javax.annotation.Nullable;

@KiwiModule(modid = Cuisine.MODID, name = "crafttweaker", dependency = "crafttweaker")
public final class CTSupport implements IModule
{
    static ArrayList<IAction> DELAYED_ACTIONS = new ArrayList<>(16);

    @Override
    public void postInit()
    {
        DELAYED_ACTIONS.forEach(CraftTweakerAPI::apply);
        DELAYED_ACTIONS.clear();
    }

    static ResourceLocation fromUserInputOrGenerate(@Nullable String name, Object... inputs)
    {
        return new ResourceLocation("crafttweaker", isEmpty(name) ? Integer.toString(Objects.hash(inputs)) : name);
    }

    static ProcessingInput fromIngredient(IIngredient ingredient)
    {
        return new CTIngredientInput(ingredient);
    }

    static OreDictDefinition fromOreEntry(IOreDictEntry entry)
    {
        return entry == null ? OreDictDefinition.EMPTY : OreDictDefinition.of(entry.getName(), entry.getAmount());
    }

    static RegularItemStackInput fromItemStack(@Nullable IItemStack ctDefinition)
    {
        return RegularItemStackInput.of(toNative(ctDefinition));
    }

    static ItemStack toNative(@Nullable IItemStack ctDefinition)
    {
        return CraftTweakerMC.getItemStack(ctDefinition);
    }

    static FluidStack toNative(ILiquidStack ctDefinition)
    {
        return CraftTweakerMC.getLiquidStack(ctDefinition);
    }

    private static boolean isEmpty(@Nullable String s)
    {
        return s == null || s.isEmpty();
    }

    static abstract class ActionWithLocator implements IAction
    {
        final ResourceLocation locator;

        ActionWithLocator(ResourceLocation locator)
        {
            this.locator = locator;
        }
    }

    static final class BulkRemoval implements IAction
    {
        private final Supplier<? extends CuisineProcessingRecipeManager<? extends CuisineProcessingRecipe>> managerAccess;

        BulkRemoval(Supplier<? extends CuisineProcessingRecipeManager<? extends CuisineProcessingRecipe>> managerAccess)
        {
            this.managerAccess = Objects.requireNonNull(managerAccess);
        }

        @Override
        public void apply()
        {
            this.managerAccess.get().removeAll();
        }

        @Override
        public String describe()
        {
            return "Removing all recipes from " + this.managerAccess.get();
        }
    }
}
