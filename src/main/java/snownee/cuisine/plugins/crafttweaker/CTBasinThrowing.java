package snownee.cuisine.plugins.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
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
    public static void add(IItemStack input, ILiquidStack inputFluid, IItemStack output)
    {
        ProcessingInput actualInput = CTSupport.fromItemStack(input);
        FluidStack actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid);
        ItemStack actualOutput = CTSupport.fromCT(output);
        CTSupport.DELAYED_ACTIONS.add(new Addition(actualInput, actualInputFluid, actualOutput));
    }

    @ZenMethod
    public static void add(IOreDictEntry input, ILiquidStack inputFluid, IItemStack output)
    {
        ProcessingInput actualInput = CTSupport.fromOreEntry(input);
        FluidStack actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid);
        ItemStack actualOutput = CTSupport.fromCT(output);
        CTSupport.DELAYED_ACTIONS.add(new Addition(actualInput, actualInputFluid, actualOutput));
    }

    private static final class Addition implements IAction
    {

        private final ProcessingInput input;
        private final FluidStack inputFluid;
        private final ItemStack output;

        private Addition(ProcessingInput input, FluidStack inputFluid, ItemStack output)
        {
            this.input = input;
            this.inputFluid = inputFluid;
            this.output = output;
        }

        @Override
        public void apply()
        {
            Processing.BASIN_THROWING.add(new SimpleThrowing(input, inputFluid, output));
        }

        @Override
        public String describe()
        {
            return null;
        }
    }
}
