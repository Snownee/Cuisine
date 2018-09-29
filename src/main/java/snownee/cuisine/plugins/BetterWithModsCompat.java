package snownee.cuisine.plugins;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;

@KiwiModule(modid = Cuisine.MODID, name = "bwm", dependency = "betterwithmods", optional = true)
public final class BetterWithModsCompat implements IModule
{

    @Override
    public void init()
    {
        if (CuisineConfig.MODULES.bwmCompat)
        {
            ItemStack bwmWoodRack = GameRegistry.makeItemStack("betterwithmods:material", 36, 1, null);
            if (bwmWoodRack.isEmpty())
            {
                return;
            }
            OreDictionary.registerOre("handleWood", bwmWoodRack);
        }
    }

}
