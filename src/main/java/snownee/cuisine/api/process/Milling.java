package snownee.cuisine.api.process;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import snownee.kiwi.crafting.input.ProcessingInput;
import snownee.kiwi.crafting.input.RegularItemStackInput;

public final class Milling extends AbstractCuisineProcessingRecipe implements CuisineProcessingRecipe
{

    private final ProcessingInput input;
    private final ItemStack output;
    private final FluidStack inputFluid, outputFluid;

    public Milling(ItemStack input, ItemStack output, @Nullable FluidStack inputFluid, @Nullable FluidStack outputFluid)
    {
        this(RegularItemStackInput.of(input), output, inputFluid, outputFluid);
    }

    public Milling(ProcessingInput input, ItemStack output, @Nullable FluidStack inputFluid, @Nullable FluidStack outputFluid)
    {
        super(new ResourceLocation("cuisine", Integer.toString(System.identityHashCode(System.in)) + "&" + System.identityHashCode(inputFluid)));
        this.input = input;
        this.output = output;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
    }

    public Milling(ItemStack input, ItemStack output)
    {
        this(input, output, null, null);
    }

    public Milling(ProcessingInput input, ItemStack output)
    {
        this(input, output, null, null);
    }

    @Nonnull
    public ProcessingInput getInput()
    {
        return input;
    }

    @Nonnull
    public ItemStack getOutput()
    {
        return output;
    }

    @Nullable
    public FluidStack getInputFluid()
    {
        return inputFluid;
    }

    @Nullable
    public FluidStack getOutputFluid()
    {
        return outputFluid;
    }

    @Override
    public boolean matches(@Nullable Object... inputs)
    {
        if (inputs == null || inputs.length != 2)
        {
            return false;
        }
        Object itemStackToCheck = inputs[0];
        if (itemStackToCheck instanceof ItemStack)
        {
            if (this.input.matches((ItemStack) itemStackToCheck))
            {
                if (this.inputFluid == null)
                {
                    return inputs[1] == null;
                }
                else
                {
                    return this.inputFluid.equals(inputs[1]) && inputFluid.amount <= ((FluidStack) inputs[1]).amount;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(@Nullable Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof Milling)
        {
            Milling another = (Milling) obj;
            if (this.input.equals(another.input))
            {
                if (this.inputFluid == null)
                {
                    return another.inputFluid == null;
                }
                else
                {
                    return this.inputFluid.isFluidEqual(another.inputFluid) && this.inputFluid.amount == another.inputFluid.amount;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return input.hashCode() * 17 + inputFluid.hashCode();
    }

    public static boolean isKnownMillingInput(ItemStack stack)
    {
        for (Milling recipe : Processing.MILLING.preview())
        {
            if (recipe.input.matches(stack))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isKnownMillingInput(@Nullable FluidStack stack)
    {
        for (Milling recipe : Processing.MILLING.preview())
        {
            if (recipe.inputFluid == null)
            {
                if (stack == null)
                {
                    return true;
                }
            }
            else
            {
                if (recipe.inputFluid.isFluidEqual(stack))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
