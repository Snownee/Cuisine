package snownee.cuisine.api.process;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import snownee.kiwi.crafting.input.ProcessingInput;
import snownee.kiwi.util.definition.ItemDefinition;

public final class Vessel extends AbstractCuisineProcessingRecipe implements CuisineProcessingRecipe
{

    private final ProcessingInput input;
    private final Fluid solvent;
    private final ItemDefinition output;
    private final FluidStack outputFluid;
    private final ProcessingInput extraRequirement;

    public Vessel(ProcessingInput input, Fluid solvent, ItemDefinition output, @Nullable FluidStack outputFluid, ProcessingInput extraRequirement)
    {
        super(new ResourceLocation("cuisine", Integer.toString(System.identityHashCode(input))));
        this.input = input;
        this.solvent = solvent;
        this.output = output;
        this.outputFluid = outputFluid;
        this.extraRequirement = extraRequirement;
    }

    public Vessel(ProcessingInput input, Fluid solvent, ItemDefinition output, @Nullable FluidStack outputFluid)
    {
        this(input, solvent, output, outputFluid, ItemDefinition.of(ItemStack.EMPTY));
    }

    public ProcessingInput getInput()
    {
        return input;
    }

    public Fluid getSolvent()
    {
        return solvent;
    }

    public ItemDefinition getOutput()
    {
        return output;
    }

    public FluidStack getOutputFluid()
    {
        return outputFluid;
    }

    public ProcessingInput getExtraRequirement()
    {
        return extraRequirement;
    }

    /**
     * 0. Input fluid
     * 1. Input item
     * 2, 3, 4... Extra items
     */
    @Override
    public boolean matches(Object... inputs)
    {
        if (inputs.length < 2)
        {
            return false;
        }
        Object firstInput = inputs[0];
        if (firstInput instanceof FluidStack && ((FluidStack) firstInput).getFluid() == this.solvent)
        {
            Object secondInput = inputs[1];
            if (secondInput instanceof ItemStack && input.matches((ItemStack) secondInput))
            {
                int countItem = ((ItemStack) secondInput).getCount();
                int countFluid = ((FluidStack) firstInput).amount;
                if (outputFluid == null)
                {
                    if (countFluid < countItem * 100)
                    {
                        return false;
                    }
                }
                else
                {
                    if (countFluid > countItem * 100)
                    {
                        return false;
                    }
                }

                if (extraRequirement.isEmpty())
                {
                    return true;
                }
                int countExtra = 0;
                for (int i = 2; i < inputs.length; i++)
                {
                    if (inputs[i] instanceof ItemStack && !((ItemStack) inputs[i]).isEmpty() && extraRequirement.matches((ItemStack) inputs[i]))
                    {
                        countExtra += ((ItemStack) inputs[i]).getCount();
                        if (countExtra >= countItem)
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Vessel vessel = (Vessel) o;

        if (!input.equals(vessel.input))
        {
            return false;
        }
        if (solvent != vessel.solvent)
        {
            return false;
        }
        return extraRequirement.isEmpty() ? vessel.extraRequirement.isEmpty()
                : extraRequirement.equals(vessel.extraRequirement);
    }

    @Override
    public int hashCode()
    {
        int result = input.hashCode();
        result = 31 * result + solvent.hashCode();
        result = 31 * result + extraRequirement.hashCode();
        return result;
    }
}
