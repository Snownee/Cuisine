package snownee.cuisine.plugins.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.process.prefab.DistillationBoiling;
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
    public static void addDistillation(ILiquidStack input, IItemStack output)
    {
        FluidStack actualInput = CraftTweakerMC.getLiquidStack(input);
        ItemStack actualOutput = CraftTweakerMC.getItemStack(output);
        CTSupport.DELAYED_ACTIONS.add(new AdditionDistillation(actualInput, actualOutput));
    }

    private static final class AdditionDistillation implements IAction
    {

        private final FluidStack input;
        private final ItemStack output;

        private AdditionDistillation(FluidStack input, ItemStack output)
        {
            this.input = input;
            this.output = output;
        }

        @Override
        public void apply()
        {
            Processing.BOILING.add(new DistillationBoiling(input, output));
        }

        @Override
        public String describe()
        {
            return null;
        }
    }
}
