package snownee.cuisine.plugins.hwyla;

import java.util.List;

import javax.annotation.Nonnull;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import snownee.cuisine.Cuisine;
import snownee.cuisine.tiles.TileBasinHeatable;

@SuppressWarnings("deprecation")
public class CuisineBasinProvider implements IWailaDataProvider
{
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getTileEntity() instanceof TileBasinHeatable)
        {
            TileBasinHeatable tileBasinHeatable = (TileBasinHeatable) accessor.getTileEntity();
            if (tileBasinHeatable.isWorking())
            {
                IFluidHandler handler = tileBasinHeatable.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (handler != null && handler.getTankProperties().length > 0 && handler.getTankProperties()[0].getContents() != null)
                {
                    tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.fluid_show", handler.getTankProperties()[0].getContents().getLocalizedName(), handler.getTankProperties()[0].getContents().amount));
                }
                tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.progress"));
                int max = tileBasinHeatable.getMaxHeatingTick();
                tooltip.add(SpecialChars.getRenderString("waila.progress", String.valueOf(max - tileBasinHeatable.getCurrentHeatingTick()), String.valueOf(max)));
            }
            else
            {
                tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.suspended"));
            }
        }
        return tooltip;
    }
}
