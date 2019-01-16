package snownee.cuisine.plugins.crafttweaker;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nullable;

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
import snownee.kiwi.util.definition.OreDictDefinition;

@KiwiModule(modid = Cuisine.MODID, name = "crafttweaker", dependency = "crafttweaker")
public final class CTSupport implements IModule
{
    static final String MODID = "crafttweaker";
    static ArrayList<IAction> DELAYED_ACTIONS = new ArrayList<>(16);

    @Override
    public void postInit()
    {
        DELAYED_ACTIONS.forEach(CraftTweakerAPI::apply);
        DELAYED_ACTIONS.clear();
    }

    static ResourceLocation fromUserInputOrGenerate(Object... inputs)
    {
        return new ResourceLocation(MODID, Integer.toString(Objects.hash(inputs)));
    }

    // Snownee: Nullable?
    static ProcessingInput fromIngredient(IIngredient ingredient)
    {
        return new CTIngredientInput(ingredient);
    }

    static OreDictDefinition fromOreEntry(IOreDictEntry entry)
    {
        return entry == null ? OreDictDefinition.EMPTY : OreDictDefinition.of(entry.getName(), entry.getAmount());
    }

    static ItemStack toNative(@Nullable IItemStack ctDefinition)
    {
        return CraftTweakerMC.getItemStack(ctDefinition);
    }

    static FluidStack toNative(ILiquidStack ctDefinition)
    {
        return CraftTweakerMC.getLiquidStack(ctDefinition);
    }

    static abstract class Addition implements IAction
    {
        final ResourceLocation locator;

        Addition(Object input0, Object... moreInputs)
        {
            this.locator = CTSupport.fromUserInputOrGenerate(input0, moreInputs);
        }
    }

    static class RemovalByIdentifier implements IAction
    {
        final CuisineProcessingRecipeManager<?> manager;
        final ResourceLocation locator;

        RemovalByIdentifier(CuisineProcessingRecipeManager<?> manager, ResourceLocation locator)
        {
            this.manager = manager;
            this.locator = locator;
        }

        @Override
        public void apply()
        {
            manager.remove(locator);
        }

        @Override
        public String describe()
        {
            return null;
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
