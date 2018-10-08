package snownee.cuisine.plugins.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import snownee.cuisine.Cuisine;
import snownee.cuisine.tiles.TileWok;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("deprecation")
public class CuisineMachineBodyInfoProvider implements IWailaDataProvider
{

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getPlayer().isCreative() && accessor.getTileEntity() instanceof TileWok)
        {
            TileWok wok = (TileWok) accessor.getTileEntity();
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.temperature", wok.getTemperature()));
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.water_amount", wok.getWaterAmount()));
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.oil_amount", wok.getOilAmount()));
        }
        return tooltip;
    }
}
