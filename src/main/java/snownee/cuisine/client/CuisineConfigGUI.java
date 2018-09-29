package snownee.cuisine.client;

import java.util.Collections;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.util.I18nUtil;

public class CuisineConfigGUI implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft minecraftInstance)
    {
        // No-op
    }

    @Override
    public boolean hasConfigGui()
    {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parent)
    {
        return new GuiConfig(parent, Cuisine.MODID, false, false, I18nUtil.translate("config.title"), CuisineConfig.class);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return Collections.emptySet();
    }
}
