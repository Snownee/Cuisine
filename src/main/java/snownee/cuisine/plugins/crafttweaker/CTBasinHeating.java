package snownee.cuisine.plugins.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.Boiling;
import snownee.cuisine.api.process.CuisineProcessingRecipeManager;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.process.prefab.DistillationBoiling;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.cuisine.BasinHeating")
@ZenRegister
public final class CTBasinHeating
{

    private CTBasinHeating()
    {
        // No-op, only used for private access
    }

    @ZenMethod
    public static void add(ILiquidStack input, IItemStack output, @Optional(valueLong = 1L) int heatValue)
    {
        FluidStack actualInput = CTSupport.toNative(input);
        ItemStack actualOutput = CTSupport.toNative(output);
        CTSupport.DELAYED_ACTIONS.add(new Addition(actualInput, actualOutput, heatValue));
    }

    @ZenMethod
    public static void remove(ILiquidStack input)
    {
        CTSupport.DELAYED_ACTIONS.add(new Removal(CTSupport.toNative(input)));
    }

    @ZenMethod
    public static void removeAll()
    {
        CTSupport.DELAYED_ACTIONS.add(new CTSupport.BulkRemoval(CTBasinHeating::getManager));
    }

    private static CuisineProcessingRecipeManager<Boiling> getManager()
    {
        return Processing.BOILING;
    }

    private static final class Addition extends CTSupport.ActionWithLocator implements IAction
    {

        private final FluidStack input;
        private final ItemStack output;
        private final int heatValue;

        private Addition(FluidStack input, ItemStack output, int heatValue)
        {
            super(input, output, heatValue);
            this.input = input;
            this.output = output;
            this.heatValue = heatValue;
        }

        @Override
        public void apply()
        {
            getManager().add(new DistillationBoiling(this.locator, input, output, heatValue));
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

    private static final class Removal implements IAction
    {

        private final FluidStack input;

        private Removal(FluidStack input)
        {
            this.input = input;
        }

        @Override
        public void apply()
        {
            getManager().remove(this.input);
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

}
