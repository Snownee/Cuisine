package snownee.cuisine.plugins.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.process.prefab.SimpleSqueezing;
import snownee.kiwi.crafting.input.ProcessingInput;
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
    public static void add(String identifier, IItemStack input, ILiquidStack output)
    {
        add(identifier, input, output, null);
    }

    @ZenMethod
    public static void add(String identifier, IOreDictEntry input, ILiquidStack output)
    {
        add(identifier, input, output, null);
    }

    @ZenMethod
    public static void add(String identifier, IItemStack input, ILiquidStack output, IItemStack extraOutput)
    {
        ProcessingInput actualInput = CTSupport.fromItemStack(input);
        FluidStack actualOutput = CTSupport.toNative(output);
        ItemStack extra = CTSupport.toNative(extraOutput);
        CTSupport.DELAYED_ACTIONS.add(new Addition(identifier, actualInput, actualOutput, extra));
    }

    @ZenMethod
    public static void add(String identifier, IOreDictEntry input, ILiquidStack output, IItemStack extraOutput)
    {
        ProcessingInput actualInput = CTSupport.fromOreEntry(input);
        FluidStack actualOutput = CTSupport.toNative(output);
        ItemStack extra = CTSupport.toNative(extraOutput);
        CTSupport.DELAYED_ACTIONS.add(new Addition(identifier, actualInput, actualOutput, extra));
    }

    private static final class Addition implements IAction
    {
        private final String identifier;
        private final ProcessingInput input;
        private final FluidStack output;
        private final ItemStack extraOutput;

        private Addition(String identifier, ProcessingInput input, FluidStack output, ItemStack extraOutput)
        {
            this.identifier = identifier;
            this.input = input;
            this.extraOutput = extraOutput;
            this.output = output;
        }

        @Override
        public void apply()
        {
            Processing.SQUEEZING.add(new SimpleSqueezing(new ResourceLocation(CraftTweaker.MODID, identifier), input, output, extraOutput));
        }

        @Override
        public String describe()
        {
            return null;
        }
    }
}
