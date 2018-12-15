package snownee.cuisine.plugins.patchouli;

import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import vazkii.patchouli.api.PatchouliAPI;

@KiwiModule(modid = Cuisine.MODID, name = "patchouli", dependency = "patchouli", optional = true)
public class ManualSupport implements IModule
{

    @Override
    public void init()
    {
        PatchouliAPI.instance.setConfigFlag("cuisine:enable_axe_chopping", CuisineConfig.PROGRESSION.axeChopping);
    }
}
