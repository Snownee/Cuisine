package snownee.cuisine.plugins.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.cuisine.api.process.CuisineProcessingRecipeManager;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.process.prefab.SimpleSqueezing;
import snownee.kiwi.crafting.input.ProcessingInput;
import snownee.kiwi.util.definition.OreDictDefinition;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.cuisine.BasinSqueezing")
@ZenRegister
public final class CTBasinSqueezing
{

    private CTBasinSqueezing()
    {
        // No-op, only used for private access
    }

    @ZenMethod
    public static void add(String identifier, IIngredient input, ILiquidStack output, @Optional IItemStack extraOutput)
    {
        ResourceLocation id = CTSupport.fromUserInputOrGenerate(identifier, input);
        ProcessingInput actualInput = CTSupport.fromIngredient(input);
        FluidStack actualOutput = CTSupport.toNative(output);
        ItemStack extra = CTSupport.toNative(extraOutput);
        CTSupport.DELAYED_ACTIONS.add(new Addition(id, actualInput, actualOutput, extra));
    }

    @ZenMethod
    public static void remove(IItemStack input)
    {
        CTSupport.DELAYED_ACTIONS.add(new RemovalByItem(CTSupport.toNative(input)));
    }

    @ZenMethod
    public static void remove(IOreDictEntry input)
    {
        CTSupport.DELAYED_ACTIONS.add(new RemovalByOre(CTSupport.fromOreEntry(input)));
    }

    @ZenMethod
    public static void removeAll()
    {
        CTSupport.DELAYED_ACTIONS.add(new CTSupport.BulkRemoval(CTBasinSqueezing::getManager));
    }

    private static CuisineProcessingRecipeManager<BasinInteracting> getManager()
    {
        return Processing.SQUEEZING;
    }

    private static final class Addition extends CTSupport.ActionWithLocator implements IAction
    {
        private final ProcessingInput input;
        private final FluidStack output;
        private final ItemStack extraOutput;

        private Addition(ResourceLocation identifier, ProcessingInput input, FluidStack output, ItemStack extraOutput)
        {
            super(identifier);
            this.input = input;
            this.extraOutput = extraOutput;
            this.output = output;
        }

        @Override
        public void apply()
        {
            Processing.SQUEEZING.add(new SimpleSqueezing(this.locator, input, output, extraOutput));
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

    private static final class RemovalByItem implements IAction
    {
        private final ItemStack input;

        private RemovalByItem(ItemStack input)
        {
            this.input = input;
        }

        @Override
        public void apply()
        {
            Processing.SQUEEZING.remove(this.input);
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

    private static final class RemovalByOre implements IAction
    {
        private final OreDictDefinition input;

        private RemovalByOre(OreDictDefinition input)
        {
            this.input = input;
        }

        @Override
        public void apply()
        {
            Processing.SQUEEZING.remove(this.input);
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

}
