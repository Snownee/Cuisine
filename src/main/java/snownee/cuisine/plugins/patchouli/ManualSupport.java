package snownee.cuisine.plugins.patchouli;

import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.ClientBookRegistry;

@KiwiModule(modid = Cuisine.MODID, name = "patchouli", dependency = "patchouli", optional = true)
public class ManualSupport implements IModule
{

    @Override
    @SideOnly(Side.CLIENT)
    public void init()
    {
        CuisineRegistry.MANUAL.setOpenManualHandler((world, player, hand) -> {
            PatchouliAPI.instance.openBookGUI(new ResourceLocation(Cuisine.MODID, "culinary_101"));
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        });

        PatchouliAPI.instance.setConfigFlag("cuisine:enable_axe_chopping", CuisineConfig.GENERAL.axeChopping);
        PatchouliAPI.instance.setConfigFlag("cuisine:enable_sunlight_heating", CuisineConfig.GENERAL.basinHeatingInDaylight);
        ClientBookRegistry.INSTANCE.pageTypes.put(Cuisine.MODID + ":centered_text", PageCenteredText.class);
    }
}
