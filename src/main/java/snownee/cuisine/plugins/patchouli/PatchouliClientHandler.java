package snownee.cuisine.plugins.patchouli;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.client.AdvancedFontRenderer;
import snownee.kiwi.util.Util;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.book.text.BookTextParser.FunctionProcessor;

@SideOnly(Side.CLIENT)
public class PatchouliClientHandler
{
    private FontRenderer originalFontRenderer;
    private static String parseError = "";

    @SubscribeEvent
    public void clientInit(ModelRegistryEvent event)
    {
        ClientBookRegistry.INSTANCE.pageTypes.put("cuisine:centered_text", PageCenteredText.class);
        ClientBookRegistry.INSTANCE.pageTypes.put("cuisine:drink_type", PageDrinkType.class);

        registerMacro((parameter, state) -> {
            List<Tuple<String, String>> tuples = parseParameters(parameter);
            if (!tuples.isEmpty())
            {
                String firstKey = tuples.get(0).getFirst();
                if (firstKey.equals("effect"))
                {
                    String effectId = tuples.get(0).getSecond();
                    Effect effect = CulinaryHub.API_INSTANCE.findEffect(effectId);
                    if (effect != null)
                    {
                        String name = Util.color(state.book.linkColor) + I18n.format(effect.getName()) + "§r";
                        String description = Util.color(effect.getColor()) + I18n.format(effect.getDescription());
                        state.tooltip = description;
                        return name;
                    }
                }
            }
            state.tooltip = parameter;
            return parseError;
        }, "cuisine");
    }

    private static void registerMacro(FunctionProcessor function, String... names)
    {
        try
        {
            Method method = BookTextParser.class.getDeclaredMethod("register", FunctionProcessor.class, String[].class);
            method.setAccessible(true);
            method.invoke(null, function, names);
        }
        catch (Exception e)
        {
            Cuisine.logger.catching(e);
        }
    }

    private static List<Tuple<String, String>> parseParameters(String s)
    {
        String[] pairs = s.split("(?<!\\\\);");
        List<Tuple<String, String>> tuples = new ArrayList<>(pairs.length);
        for (String pair : pairs)
        {
            if (pair.isEmpty())
            {
                continue;
            }
            String[] elements = pair.split("=", 2);
            if (elements.length == 2)
            {
                tuples.add(new Tuple<String, String>(elements[0], elements[1]));
            }
        }
        return tuples;
    }

    @SuppressWarnings("unused")
    private static String getParameter(List<Tuple<String, String>> tuples, String key)
    {
        for (Tuple<String, String> tuple : tuples)
        {
            if (tuple.getFirst().equals(key))
            {
                return tuple.getSecond();
            }
        }
        return null;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiOpen(GuiOpenEvent event)
    {
        GuiScreen gui = event.getGui();
        if (gui == null && originalFontRenderer != null)
        {
            Minecraft.getMinecraft().fontRenderer = originalFontRenderer;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiInit(InitGuiEvent.Pre event)
    {
        GuiScreen gui = event.getGui();
        if (gui instanceof GuiBook && gui.fontRenderer.getClass() != AdvancedFontRenderer.class)
        {
            parseError = "§4" + I18nUtil.translate("manual.parseError") + "§r";
            // Yes it will replace all manual which uses Patchouli. If it has problem, I will fix then
            gui.fontRenderer = AdvancedFontRenderer.INSTANCE;
            originalFontRenderer = Minecraft.getMinecraft().fontRenderer;
            Minecraft.getMinecraft().fontRenderer = gui.fontRenderer;
        }
    }
}
