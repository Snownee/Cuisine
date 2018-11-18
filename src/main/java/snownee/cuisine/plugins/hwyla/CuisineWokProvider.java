package snownee.cuisine.plugins.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import snownee.cuisine.tiles.TileWok;
import snownee.cuisine.util.I18nUtil;

import javax.annotation.Nonnull;
import java.util.List;

final class CuisineWokProvider implements IWailaDataProvider
{

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getPlayer().isCreative() && accessor.getTileEntity() instanceof TileWok)
        {
            NBTTagCompound data = accessor.getNBTData();
            tooltip.add(TextFormatting.GRAY + I18nUtil.translate("gui.temperature", data.getInteger("temperature")));
            tooltip.add(TextFormatting.GRAY + I18nUtil.translate("gui.water_amount", data.getInteger("water")));
            tooltip.add(TextFormatting.GRAY + I18nUtil.translate("gui.oil_amount", data.getInteger("oil")));
        }
        return tooltip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
    {
        if (te instanceof TileWok)
        {
            TileWok wok = (TileWok) te;
            tag.setInteger("temperature", wok.getTemperature());
            tag.setInteger("water", wok.getWaterAmount());
            tag.setInteger("oil", wok.getOilAmount());
        }
        return tag;
    }
}
