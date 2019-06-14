package snownee.cuisine.plugins.patchouli;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import vazkii.patchouli.api.PatchouliAPI;

@KiwiModule(modid = Cuisine.MODID, name = "patchouli", dependency = "patchouli", optional = true)
public class ManualSupport implements IModule
{
    @Override
    @SideOnly(Side.CLIENT)
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(new PatchouliClientHandler());
    }

    @Override
    public void init()
    {
        CuisineRegistry.MANUAL.setOpenManualHandler((world, player, hand) -> {
            if (player instanceof EntityPlayerMP)
            {
                PatchouliAPI.instance.openBookGUI((EntityPlayerMP) player, new ResourceLocation(Cuisine.MODID, "culinary_101"));
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        });

        PatchouliAPI.instance.setConfigFlag("cuisine:enable_axe_chopping", CuisineConfig.GENERAL.axeChopping);
        PatchouliAPI.instance.setConfigFlag("cuisine:enable_sunlight_heating", CuisineConfig.GENERAL.basinHeatingInDaylight);
        PatchouliAPI.instance.setConfigFlag("cuisine:enable_fruit_dropping", CuisineConfig.GENERAL.fruitDrops);
        PatchouliAPI.instance.setConfigFlag("cuisine:enable_garden_world_gen", CuisineConfig.WORLD_GEN.cropsGenRate > 0 && CuisineConfig.WORLD_GEN.cropsGenDimensions.length > 0);
        PatchouliAPI.instance.setConfigFlag("cuisine:enable_grass_seeds", CuisineConfig.GENERAL.basicSeedsWeight > 0);
        PatchouliAPI.instance.setConfigFlag("cuisine:drinkro_uses_energy", CuisineConfig.GENERAL.drinkroUsesFE > 0);
        PatchouliAPI.instance.setConfigFlag("cuisine:squeezer_uses_energy", CuisineConfig.GENERAL.squeezerUsesFE > 0);
    }
}
