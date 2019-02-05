package snownee.cuisine.plugins.patchouli;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.Effect;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.client.AdvancedFontRenderer;
import snownee.kiwi.util.Util;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.book.text.BookTextParser.FunctionProcessor;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.ItemStackUtil.StackWrapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
@SideOnly(Side.CLIENT)
public class PatchouliClientHandler implements IResourceManagerReloadListener
{
    private FontRenderer originalFontRenderer;
    private static String parseError = "";
    private boolean loaded = false;

    PatchouliClientHandler()
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

        IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
        if (manager instanceof IReloadableResourceManager)
            ((IReloadableResourceManager) manager).registerReloadListener(this);
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

    public void reload()
    {
        Book book = BookRegistry.INSTANCE.books.get(new ResourceLocation(Cuisine.MODID, "culinary_101"));
        if (book != null)
        {
            registerSubtypesMapping(book, CuisineRegistry.CROPS, "preface/plant");
            registerMapping(book, new ItemStack(CuisineRegistry.BAMBOO), "preface/plant");
            registerMapping(book, new ItemStack(Blocks.FARMLAND), "preface/plant");

            registerMapping(book, new ItemStack(CuisineRegistry.WOODEN_BASIN), "processing/basin");
            registerMapping(book, new ItemStack(CuisineRegistry.EARTHEN_BASIN), "processing/basin");
            registerSubtypesMapping(book, CuisineRegistry.EARTHEN_BASIN_COLORED, "processing/basin");
            registerMapping(book, new ItemStack(CuisineRegistry.ITEM_MORTAR), "processing/mortar");
            registerMapping(book, new ItemStack(CuisineRegistry.JAR), "processing/jar");
            registerMapping(book, new ItemStack(CuisineRegistry.MILL), "processing/mill");
            registerMapping(book, new ItemStack(CuisineRegistry.CHOPPING_BOARD), "processing/chopping_board");

            registerMapping(book, new ItemStack(Items.BREWING_STAND), "utensils/brewing");
            registerMapping(book, new ItemStack(CuisineRegistry.DISH), "utensils/plate");
            registerMapping(book, new ItemStack(CuisineRegistry.DRINKRO), "utensils/drinkro");
            registerMapping(book, new ItemStack(CuisineRegistry.FIRE_PIT, 1, 0), "utensils/fire_pit");
            registerMapping(book, new ItemStack(CuisineRegistry.FIRE_PIT, 1, 1), "utensils/wok");
            registerMapping(book, new ItemStack(CuisineRegistry.FIRE_PIT, 1, 2), "utensils/bbq_rack");
            registerMapping(book, new ItemStack(CuisineRegistry.FIRE_PIT, 1, 3), "utensils/frying_pan");

            registerSubtypesMapping(book, CuisineRegistry.SHEARED_LEAVES, "misc/fruit_trees");
            registerSubtypesMapping(book, CuisineRegistry.SAPLING, "misc/fruit_trees");
            registerMapping(book, new ItemStack(CuisineRegistry.LOG), "misc/fruit_trees");
            registerMapping(book, new ItemStack(CuisineRegistry.TOFU_BLOCK), "misc/tofu_block");
            registerMapping(book, new ItemStack(Blocks.DISPENSER), "misc/wooden_arm");
        }
    }

    private static void registerMapping(Book book, ItemStack stack, String entry)
    {
        BookEntry bookEntry = book.contents.entries.get(new ResourceLocation(Cuisine.MODID, entry));
        if (bookEntry != null)
        {
            book.contents.recipeMappings.put(new StackWrapper(stack), Pair.of(bookEntry, 0));
        }
    }

    private static void registerSubtypesMapping(Book book, Block block, String entry)
    {
        NonNullList<ItemStack> items = NonNullList.create();
        block.getSubBlocks(block.getCreativeTab(), items);
        for (ItemStack stack : items)
        {
            registerMapping(book, stack, entry);
        }
    }

    @SuppressWarnings("unused")
    private static void registerSubtypesMapping(Book book, Item item, String entry)
    {
        NonNullList<ItemStack> items = NonNullList.create();
        item.getSubItems(item.getCreativeTab(), items);
        for (ItemStack stack : items)
        {
            registerMapping(book, stack, entry);
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        if (loaded)
        {
            reload();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (!loaded)
        {
            loaded = true;
            reload();
        }
    }
}
