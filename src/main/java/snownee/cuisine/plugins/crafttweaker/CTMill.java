package snownee.cuisine.plugins.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.Milling;
import snownee.cuisine.api.process.Processing;
import snownee.kiwi.util.definition.OreDictDefinition;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.cuisine.Mill")
@ZenRegister
public class CTMill
{

    @ZenMethod
    public static void add(IItemStack input, ILiquidStack inputFluid, IItemStack output, ILiquidStack outputFluid)
    {
        ItemStack actualInput = CraftTweakerMC.getItemStack(input);
        ItemStack actualOutput = CraftTweakerMC.getItemStack(output);
        FluidStack actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid);
        FluidStack actualOutputFluid = CraftTweakerMC.getLiquidStack(outputFluid);
        CTSupport.DELAYED_ACTIONS.add(new ItemBasedAddition(actualInput, actualInputFluid, actualOutput, actualOutputFluid));
    }

    @ZenMethod
    public static void add(IOreDictEntry input, ILiquidStack inputFluid, IItemStack output, ILiquidStack outputFluid)
    {
        OreDictDefinition actualInput = OreDictDefinition.of(input.getName(), input.getAmount());
        ItemStack actualOutput = CraftTweakerMC.getItemStack(output);
        FluidStack actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid);
        FluidStack actualOutputFluid = CraftTweakerMC.getLiquidStack(outputFluid);
        CTSupport.DELAYED_ACTIONS.add(new OreDictBasedAddition(actualInput, actualInputFluid, actualOutput, actualOutputFluid));
    }

    @ZenMethod
    public static void remove(IItemStack input, ILiquidStack inputFluid)
    {
        ItemStack actualInput = CraftTweakerMC.getItemStack(input);
        FluidStack actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid);
        CTSupport.DELAYED_ACTIONS.add(new ItemBasedRemoval(actualInput, actualInputFluid));
    }

    @ZenMethod
    public static void remove(IOreDictEntry input, ILiquidStack inputFluid)
    {
        OreDictDefinition actualInput = OreDictDefinition.of(input.getName(), input.getAmount());
        FluidStack actualInputFluid = CraftTweakerMC.getLiquidStack(inputFluid);
        CTSupport.DELAYED_ACTIONS.add(new OreDictBasedRemoval(actualInput, actualInputFluid));
    }

    @ZenMethod
    public static void removeAll()
    {
        CTSupport.DELAYED_ACTIONS.add(new RemoveAll());
    }

    private static final class ItemBasedAddition implements IAction
    {

        private final ItemStack actualInput;
        private final FluidStack actualInputFluid;
        private final ItemStack actualOutput;
        private final FluidStack actualOutputFluid;

        ItemBasedAddition(ItemStack actualInput, FluidStack actualInputFluid, ItemStack actualOutput, FluidStack actualOutputFluid)
        {
            this.actualInput = actualInput;
            this.actualInputFluid = actualInputFluid;
            this.actualOutput = actualOutput;
            this.actualOutputFluid = actualOutputFluid;
        }

        @Override
        public void apply()
        {
            Processing.MILLING.add(new Milling(actualInput, actualOutput, actualInputFluid, actualOutputFluid));
        }

        @Override
        public String describe()
        {
            return String.format("Add Cuisine Mill recipe: %s + %s -> %s + %s", actualInput, actualInputFluid, actualOutput, actualOutputFluid);
        }
    }

    private static final class OreDictBasedAddition implements IAction
    {
        private final OreDictDefinition actualInput;
        private final FluidStack actualInputFluid;
        private final ItemStack actualOutput;
        private final FluidStack actualOutputFluid;

        OreDictBasedAddition(OreDictDefinition actualInput, FluidStack actualInputFluid, ItemStack actualOutput, FluidStack actualOutputFluid)
        {
            this.actualInput = actualInput;
            this.actualInputFluid = actualInputFluid;
            this.actualOutput = actualOutput;
            this.actualOutputFluid = actualOutputFluid;
        }

        @Override
        public void apply()
        {
            Processing.MILLING.add(new Milling(actualInput, actualOutput, actualInputFluid, actualOutputFluid));
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
            Processing.MILLING.remove(new Milling(actualInput, ItemStack.EMPTY, actualInputFluid, null));
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
            Processing.MILLING.remove(new Milling(actualInput, ItemStack.EMPTY, actualInputFluid, null));
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

    private static final class RemoveAll implements IAction
    {

        @Override
        public void apply()
        {
            Processing.MILLING.removeAll();
        }

        @Override
        public String describe()
        {
            return "Removing all Cuisine milling recipes";
        }
    }

}
