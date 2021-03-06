package snownee.cuisine.plugins.crafttweaker;

import javax.annotation.Nonnull;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.api.process.Chopping;
import snownee.cuisine.api.process.CuisineProcessingRecipeManager;
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
    public static void remove(@Nonnull String identifier)
    {
        CTSupport.DELAYED_ACTIONS.add(new CTSupport.RemovalByIdentifier(getManager(), new ResourceLocation(identifier)));
    }

    @ZenMethod
    public static void removeByOutput(@Nonnull IIngredient output)
    {
        CTSupport.DELAYED_ACTIONS.add(new RemovalByOutput(output));
    }

    @ZenMethod
    public static void removeAll()
    {
        CTSupport.DELAYED_ACTIONS.add(new CTSupport.BulkRemoval(CTAxeChopping::getManager));
    }

    private static CuisineProcessingRecipeManager<Chopping> getManager()
    {
        return Processing.CHOPPING;
    }

    @ZenMethod
    public static boolean isEnabled()
    {
        return CuisineConfig.GENERAL.axeChopping;
    }

    @ZenMethod
    public static int getDefaultPlanksOutput()
    {
        return CuisineConfig.GENERAL.axeChoppingPlanksOutput;
    }

    @ZenMethod
    public static int getDefaultStickOutput()
    {
        return CuisineConfig.GENERAL.axeChoppingStickOutput;
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
            getManager().add(new Chopping(new ResourceLocation(CTSupport.MODID, Integer.toString(System.identityHashCode(input))), RegularItemStackInput.of(input), output));
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
            getManager().add(new Chopping(new ResourceLocation(CTSupport.MODID, Integer.toString(System.identityHashCode(input))), input, output));
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
            getManager().remove(input);
        }

        @Override
        public String describe()
        {
            return String.format("Remove all Cuisine Axe-Chopping recipes that has input of %s", input);
        }
    }

    private static final class RemovalByOutput implements IAction
    {
        final IIngredient output;

        RemovalByOutput(IIngredient output)
        {
            this.output = output;
        }

        @Override
        public void apply()
        {
            getManager().removeIf(r -> output.matches(CraftTweakerMC.getIItemStack(r.getOutput())));
        }

        @Override
        public String describe()
        {
            return null;
        }
    }

}
