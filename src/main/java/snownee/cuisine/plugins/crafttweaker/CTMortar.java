package snownee.cuisine.plugins.crafttweaker;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import snownee.cuisine.api.process.CuisineProcessingRecipeManager;
import snownee.cuisine.api.process.Grinding;
import snownee.cuisine.api.process.Processing;
import snownee.kiwi.crafting.input.ProcessingInput;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.cuisine.Mortar")
@ZenRegister
public class CTMortar
{
    @ZenMethod
    public static void add(IIngredient[] inputs, IItemStack output, int step)
    {
        List<ProcessingInput> list = Arrays.stream(inputs).map(CTSupport::fromIngredient).collect(Collectors.toList());
        CTSupport.DELAYED_ACTIONS.add(new Addition(list, CraftTweakerMC.getItemStack(output), step));
    }

    @ZenMethod
    public static void remove(IItemStack[] inputs)
    {
        CTSupport.DELAYED_ACTIONS.add(new Removal(Arrays.stream(inputs).map(CTSupport::toNative).collect(Collectors.toList())));
    }

    @ZenMethod
    public static void removeByOutput(@Nonnull IIngredient output)
    {
        CTSupport.DELAYED_ACTIONS.add(new RemovalByOutput(output));
    }

    @ZenMethod
    public static void remove(@Nonnull String identifier)
    {
        CTSupport.DELAYED_ACTIONS.add(new CTSupport.RemovalByIdentifier(getManager(), new ResourceLocation(identifier)));
    }

    @ZenMethod
    public static void removeAll()
    {
        CTSupport.DELAYED_ACTIONS.add(new CTSupport.BulkRemoval(CTMortar::getManager));
    }

    private static CuisineProcessingRecipeManager<Grinding> getManager()
    {
        return Processing.GRINDING;
    }

    private static final class Addition extends CTSupport.Addition implements IAction
    {
        final List<ProcessingInput> inputs;
        final ItemStack output;
        final int step;

        Addition(List<ProcessingInput> inputs, ItemStack output, int step)
        {
            super(inputs, output, step);
            this.inputs = inputs;
            this.output = output;
            this.step = step;
        }

        @Override
        public void apply()
        {
            getManager().add(new Grinding(this.locator, inputs, output, step));
        }

        @Override
        public String describe()
        {
            return String.format("Add Cuisine Mortar recipe: %s -> %s", inputs, output);
        }
    }

    private static final class Removal implements IAction
    {
        final List<ItemStack> inputs;

        Removal(List<ItemStack> inputs)
        {
            this.inputs = inputs;
        }

        @Override
        public void apply()
        {
            getManager().remove(inputs.toArray());
        }

        @Override
        public String describe()
        {
            return String.format("Remove Cuisine Mortar recipe that has input of %s", inputs);
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
