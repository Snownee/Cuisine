package snownee.cuisine.plugins.crafttweaker;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import snownee.kiwi.crafting.input.ProcessingInput;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

final class CTIngredientInput implements ProcessingInput
{
    private final IIngredient ingredient;

    CTIngredientInput(IIngredient ingredient)
    {
        this.ingredient = ingredient;
    }

    @Nonnull
    @Override
    public List<ItemStack> examples()
    {
        return Optional.ofNullable(this.ingredient.getItems())
                .orElse(Collections.emptyList())
                .stream()
                .map(CTSupport::toNative)
                .collect(Collectors.toList());
    }

    @Override
    public boolean matches(@Nonnull ItemStack itemStack)
    {
        return this.ingredient.matches(CraftTweakerMC.getIItemStack(itemStack));
    }

    @Override
    public boolean isEmpty()
    {
        return this.ingredient != null && this.ingredient.getAmount() > 0;
    }

    @Override
    public int count()
    {
        return this.ingredient.getAmount();
    }
}
