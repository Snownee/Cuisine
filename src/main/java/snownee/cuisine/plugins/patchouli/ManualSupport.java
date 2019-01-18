package snownee.cuisine.plugins.patchouli;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.client.AdvancedFontRenderer;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.gui.GuiBook;

@KiwiModule(modid = Cuisine.MODID, name = "patchouli", dependency = "patchouli", optional = true)
public class ManualSupport implements IModule
{
    private FontRenderer originalFontRenderer;

    public ManualSupport()
    {
        MinecraftForge.EVENT_BUS.register(this);
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
        PatchouliAPI.instance.setConfigFlag("cuisine:drinkro_uses_energy", CuisineConfig.GENERAL.drinkroUsesFE > 0);
        PatchouliAPI.instance.setConfigFlag("cuisine:squeezer_uses_energy", CuisineConfig.GENERAL.squeezerUsesFE > 0);
        // TODO (for someone from the future): to safely call this, you need a proper sided proxy, not using @SideOnly hack...
        ClientBookRegistry.INSTANCE.pageTypes.put(Cuisine.MODID + ":centered_text", PageCenteredText.class);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public void onGuiOpen(GuiOpenEvent event)
    {
        GuiScreen gui = event.getGui();
        if (gui == null && originalFontRenderer != null)
        {
            Minecraft.getMinecraft().fontRenderer = originalFontRenderer;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public void onGuiInit(InitGuiEvent.Pre event)
    {
        GuiScreen gui = event.getGui();
        if (gui instanceof GuiBook && gui.fontRenderer.getClass() != AdvancedFontRenderer.class)
        {
            // Yes it will replace all manual which uses Patchouli. If it has problem, I will fix then
            gui.fontRenderer = AdvancedFontRenderer.INSTANCE;
            originalFontRenderer = Minecraft.getMinecraft().fontRenderer;
            Minecraft.getMinecraft().fontRenderer = gui.fontRenderer;
        }
    }
}
