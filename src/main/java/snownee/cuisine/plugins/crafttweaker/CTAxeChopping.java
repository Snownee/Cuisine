package snownee.cuisine.plugins.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import snownee.cuisine.api.process.Chopping;
import snownee.cuisine.api.process.Processing;
import snownee.kiwi.crafting.input.RegularItemStackInput;
import snownee.kiwi.util.definition.OreDictDefinition;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.cuisine.AxeChopping")
@ZenRegister
public class CTAxeChopping
{
    @ZenMethod
    public static void add(IItemStack input, IItemStack output)
    {
        ItemStack actualInput = CTSupport.toNative(input);
        ItemStack actualOutput = CTSupport.toNative(output);
        CTSupport.DELAYED_ACTIONS.add(new ItemBasedAddition(actualInput, actualOutput));
    }

    @ZenMethod
    public static void add(IOreDictEntry input, IItemStack output)
    {
        OreDictDefinition actualInput = CTSupport.fromOreEntry(input);
        ItemStack actualOutput = CTSupport.toNative(output);
        CTSupport.DELAYED_ACTIONS.add(new OreDictBasedAddition(actualInput, actualOutput));
    }

    @ZenMethod
    public static void remove(IItemStack input)
    {
        ItemStack actualInput = CTSupport.toNative(input);
        CTSupport.DELAYED_ACTIONS.add(new ItemBasedRemoval(actualInput));
    }

    @ZenMethod
    public static void remove(IOreDictEntry input)
    {
        OreDictDefinition actualInput = CTSupport.fromOreEntry(input);
        CTSupport.DELAYED_ACTIONS.add(new OreDictBasedRemoval(actualInput));
    }

    @ZenMethod
    public static void removeAll()
    {
        CTSupport.DELAYED_ACTIONS.add(new BulkRemoval());
    }

    private static final class ItemBasedAddition implements IAction
    {
        private final ItemStack input;
        private final ItemStack output;

        private ItemBasedAddition(ItemStack input, ItemStack output)
        {
            this.input = input;
            this.output = output;
        }

        @Override
        public void apply()
        {
            Processing.CHOPPING.add(new Chopping(RegularItemStackInput.of(input), output));
        }

        @Override
        public String describe()
        {
            return String.format("Add Cuisine Axe-Chopping recipe: input %s -> output %s", input, output);
        }
    }

    private static final class OreDictBasedAddition implements IAction
    {
        private final OreDictDefinition input;
        private final ItemStack output;

        private OreDictBasedAddition(OreDictDefinition input, ItemStack output)
        {
            this.input = input;
            this.output = output;
        }

        @Override
        public void apply()
        {
            Processing.CHOPPING.add(new Chopping(input, output));
        }

        @Override
        public String describe()
        {
            return String.format("Add Cuisine Axe-Chopping recipe: input %s -> output %s", input, output);
        }
    }

    private static final class ItemBasedRemoval implements IAction
    {
        private final ItemStack input;

        private ItemBasedRemoval(ItemStack input)
        {
            this.input = input;
        }

        @Override
        public void apply()
        {
            Processing.CHOPPING.remove(new Chopping(RegularItemStackInput.of(input), ItemStack.EMPTY));
        }

        @Override
        public String describe()
        {
            return String.format("Remove all Cuisine Axe-Chopping recipes that has input of %s", input);
        }
    }

    private static final class OreDictBasedRemoval implements IAction
    {
        private final OreDictDefinition input;

        private OreDictBasedRemoval(OreDictDefinition input)
        {
            this.input = input;
        }

        @Override
        public void apply()
        {
            Processing.CHOPPING.remove(new Chopping(input, ItemStack.EMPTY));
        }

        @Override
        public String describe()
        {
            return String.format("Remove all Cuisine Axe-Chopping recipes that has input of %s", input);
        }
    }

    private static final class BulkRemoval implements IAction
    {

        @Override
        public void apply()
        {
            Processing.CHOPPING.removeAll();
        }

        @Override
        public String describe()
        {
            return "Remove all Cuisine Axe-Chopping recipes";
        }
    }
}
