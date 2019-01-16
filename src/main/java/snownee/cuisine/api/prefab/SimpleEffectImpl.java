package snownee.cuisine.api.prefab;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.Effect;
import snownee.cuisine.api.EffectCollector;
import snownee.cuisine.api.Ingredient;

public class SimpleEffectImpl implements Effect
{
    private final String name;
    private final int color;

    public SimpleEffectImpl(String name, int color)
    {
        this.name = name;
        this.color = color;
    }

    @Override
    public void onEaten(ItemStack stack, EntityPlayer player, CompositeFood food, List<Ingredient> ingredients, EffectCollector collector)
    {
        // Do nothing by default
    }

    @Override
    public int getPriority()
    {
        return 0;
    }

    @Override
    public String getID()
    {
        return name;
    }

    @Override
    public String getName()
    {
        return "cuisine.effect." + name + ".name";
    }

    @Override
    public String getDescription()
    {
        return "cuisine.effect." + name + ".tip";
    }

    @Override
    public int getColor()
    {
        return this.color;
    }

}
