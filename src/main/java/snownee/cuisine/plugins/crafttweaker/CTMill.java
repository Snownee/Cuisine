package snownee.cuisine.plugins.crafttweaker;

import javax.annotation.Nonnull;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.CuisineProcessingRecipeManager;
import snownee.cuisine.api.process.Milling;
import snownee.cuisine.api.process.Processing;
import snownee.kiwi.crafting.input.ProcessingInput;
import snownee.kiwi.util.definition.OreDictDefinition;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.cuisine.Mill")
@ZenRegister
public class CTMill
{

    @ZenMethod
    public static void add(String identifier, IIngredient input, ILiquidStack inputFluid, IItemStack output, ILiquidStack outputFluid)
    {
        ResourceLocation id = CTSupport.fromUserInputOrGenerate(identifier, input, inputFluid);
        ProcessingInput actualInput = CTSupport.fromIngredient(input);
        ItemStack actualOutput = CTSupport.toNative(output);
        FluidStack actualInputFluid = CTSupport.toNative(inputFluid);
        FluidStack actualOutputFluid = CTSupport.toNative(outputFluid);
        CTSupport.DELAYED_ACTIONS.add(new Addition(id, actualInput, actualInputFluid, actualOutput, actualOutputFluid));
    }

    @ZenMethod
    public static void remove(IItemStack input, ILiquidStack inputFluid)
    {
        ItemStack actualInput = CTSupport.toNative(input);
        FluidStack actualInputFluid = CTSupport.toNative(inputFluid);
        CTSupport.DELAYED_ACTIONS.add(new ItemBasedRemoval(actualInput, actualInputFluid));
    }

    @ZenMethod
    public static void remove(IOreDictEntry input, ILiquidStack inputFluid)
    {
        OreDictDefinition actualInput = OreDictDefinition.of(input.getName(), input.getAmount());
        FluidStack actualInputFluid = CTSupport.toNative(inputFluid);
        CTSupport.DELAYED_ACTIONS.add(new OreDictBasedRemoval(actualInput, actualInputFluid));
    }

    @ZenMethod
    public static void remove(@Nonnull String identifier)
    {
        CTSupport.DELAYED_ACTIONS.add(new CTSupport.RemovalByIdentifier(getManager(), new ResourceLocation(identifier)));
    }

    @ZenMethod
    public static void removeAll()
    {
        CTSupport.DELAYED_ACTIONS.add(new CTSupport.BulkRemoval(CTMill::getManager));
    }

    private static CuisineProcessingRecipeManager<Milling> getManager()
    {
        return Processing.MILLING;
    }

    private static final class Addition extends CTSupport.Addition
    {
        private final ProcessingInput actualInput;
        private final FluidStack actualInputFluid;
        private final ItemStack actualOutput;
        private final FluidStack actualOutputFluid;

        Addition(ResourceLocation id, ProcessingInput actualInput, FluidStack actualInputFluid, ItemStack actualOutput, FluidStack actualOutputFluid)
        {
            super(actualInput, actualInputFluid, actualOutput, actualOutputFluid);
            this.actualInput = actualInput;
            this.actualInputFluid = actualInputFluid;
            this.actualOutput = actualOutput;
            this.actualOutputFluid = actualOutputFluid;
        }

        @Override
        public void apply()
        {
            getManager().add(new Milling(this.locator, actualInput, actualOutput, actualInputFluid, actualOutputFluid));
        }

        @Override
        public String describe()
        {
            return String.format("Add Cuisine Mill recipe: %s + %s -> %s + %s", actualInput, actualInputFluid, actualOutput, actualOutputFluid);
        }
    }

    private static final class ItemBasedRemoval implements IAction
    {
        private final ItemStack actualInput;
        private final FluidStack actualInputFluid;

        ItemBasedRemoval(ItemStack actualInput, FluidStack actualInputFluid)
        {
            this.actualInput = actualInput;
            this.actualInputFluid = actualInputFluid;
        }

        @Override
        public void apply()
        {
            getManager().remove(new Milling(new ResourceLocation(CTSupport.MODID), actualInput, ItemStack.EMPTY, actualInputFluid, null));
        }

        @Override
        public String describe()
        {
            return String.format("Remove all Cuisine milling recipes that has input item of %s and input fluid of %s", actualInput, actualInputFluid);
        }
    }

    private static class OreDictBasedRemoval implements IAction
    {
        private final OreDictDefinition actualInput;
        private final FluidStack actualInputFluid;

        OreDictBasedRemoval(OreDictDefinition actualInput, FluidStack actualInputFluid)
        {
            this.actualInput = actualInput;
            this.actualInputFluid = actualInputFluid;
        }

        @Override
        public void apply()
        {
            getManager().remove(new Milling(new ResourceLocation(CTSupport.MODID), actualInput, ItemStack.EMPTY, actualInputFluid, null));
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

}
