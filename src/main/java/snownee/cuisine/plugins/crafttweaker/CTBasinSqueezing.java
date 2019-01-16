package snownee.cuisine.plugins.crafttweaker;

import javax.annotation.Nonnull;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.BasinInteracting;
import snownee.cuisine.api.process.CuisineProcessingRecipeManager;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.process.prefab.SimpleSqueezing;
import snownee.kiwi.crafting.input.ProcessingInput;
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
    public static void add(IIngredient input, ILiquidStack output, @Optional IItemStack extraOutput)
    {
        ProcessingInput actualInput = CTSupport.fromIngredient(input);
        FluidStack actualOutput = CTSupport.toNative(output);
        ItemStack extra = CTSupport.toNative(extraOutput);
        CTSupport.DELAYED_ACTIONS.add(new Addition(actualInput, actualOutput, extra));
    }

    @ZenMethod
    public static void remove(IItemStack input, ILiquidStack inputFluid)
    {
        CTSupport.DELAYED_ACTIONS.add(new RemovalByItem(CTSupport.toNative(input), CTSupport.toNative(inputFluid)));
    }

    @ZenMethod
    public static void remove(@Nonnull String identifier)
    {
        CTSupport.DELAYED_ACTIONS.add(new CTSupport.RemovalByIdentifier(getManager(), new ResourceLocation(identifier)));
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

    private static final class Addition extends CTSupport.Addition implements IAction
    {
        private final ProcessingInput input;
        private final FluidStack output;
        private final ItemStack extraOutput;

        private Addition(ProcessingInput input, FluidStack output, ItemStack extraOutput)
        {
            super(input, output, extraOutput);
            this.input = input;
            this.extraOutput = extraOutput;
            this.output = output;
        }

        @Override
        public void apply()
        {
            getManager().add(new SimpleSqueezing(this.locator, input, output, extraOutput));
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
        private final FluidStack inputFluid;

        RemovalByItem(ItemStack input, FluidStack inputFluid)
        {
            this.input = input;
            this.inputFluid = inputFluid;
        }

        @Override
        public void apply()
        {
            getManager().remove(this.input, this.inputFluid);
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

}
