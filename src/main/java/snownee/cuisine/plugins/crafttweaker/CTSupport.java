package snownee.cuisine.plugins.crafttweaker;

import java.util.ArrayList;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import snownee.cuisine.Cuisine;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.crafting.input.RegularItemStackInput;
import snownee.kiwi.util.definition.OreDictDefinition;

@KiwiModule(modid = Cuisine.MODID, name = "crafttweaker", dependency = "crafttweaker")
public final class CTSupport implements IModule
{
    static ArrayList<IAction> DELAYED_ACTIONS = new ArrayList<>(16);

    @Override
    public void postInit()
    {
        DELAYED_ACTIONS.forEach(CraftTweakerAPI::apply);
        DELAYED_ACTIONS.clear();
    }

    public static OreDictDefinition fromOreEntry(IOreDictEntry entry)
    {
        return entry == null ? OreDictDefinition.EMPTY : OreDictDefinition.of(entry.getName(), entry.getAmount());
    }

    public static RegularItemStackInput fromItemStack(IItemStack ctDefinition)
    {
        return RegularItemStackInput.of(fromCT(ctDefinition));
    }

    public static ItemStack fromCT(IItemStack ctDefinition)
    {
        return CraftTweakerMC.getItemStack(ctDefinition);
    }

}
