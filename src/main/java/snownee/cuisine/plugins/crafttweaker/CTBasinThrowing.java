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
import snownee.cuisine.api.process.prefab.SimpleThrowing;
import snownee.kiwi.crafting.input.ProcessingInput;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.cuisine.BasinThrowing")
@ZenRegister
public final class CTBasinThrowing
{

    private CTBasinThrowing()
    {
        // No-op, only used for private access
    }

    @ZenMethod
    public static void add(IIngredient input, ILiquidStack inputFluid, IItemStack output)
    {
        ProcessingInput actualInput = CTSupport.fromIngredient(input);
        FluidStack actualInputFluid = CTSupport.toNative(inputFluid);
        ItemStack actualOutput = CTSupport.toNative(output);
        CTSupport.DELAYED_ACTIONS.add(new Addition(actualInput, actualInputFluid, actualOutput));
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
        CTSupport.DELAYED_ACTIONS.add(new CTSupport.BulkRemoval(CTBasinThrowing::getManager));
    }

    private static CuisineProcessingRecipeManager<BasinInteracting> getManager()
    {
        return Processing.BASIN_THROWING;
    }

    private static final class Addition extends CTSupport.Addition implements IAction
    {
        private final ProcessingInput input;
        private final FluidStack inputFluid;
        private final ItemStack output;

        private Addition(ProcessingInput input, FluidStack inputFluid, ItemStack output)
        {
            super(input, inputFluid, output);
            this.input = input;
            this.inputFluid = inputFluid;
            this.output = output;
        }

        @Override
        public void apply()
        {
            getManager().add(new SimpleThrowing(this.locator, input, inputFluid, output));
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
