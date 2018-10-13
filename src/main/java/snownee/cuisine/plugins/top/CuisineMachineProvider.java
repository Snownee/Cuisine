package snownee.cuisine.plugins.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.blocks.BlockFirePit;
import snownee.cuisine.tiles.TileBasinHeatable;
import snownee.cuisine.tiles.TileWok;

import java.text.MessageFormat;

@SuppressWarnings("deprecation")
public class CuisineMachineProvider implements IProbeInfoProvider
{

    @Override
    public String getID()
    {
        return Cuisine.MODID + ":machine";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {
        if (player.isCreative() && blockState.getBlock() instanceof BlockFirePit)
        {
            TileEntity tile = world.getTileEntity(data.getPos());
            if (tile instanceof TileWok)
            {
                TileWok tileWok = (TileWok) tile;
                probeInfo.text(TextStyleClass.LABEL + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.temperature", tileWok.getTemperature()));
                probeInfo.text(TextStyleClass.LABEL + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.water_amount", tileWok.getWaterAmount()));
                probeInfo.text(TextStyleClass.LABEL + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.oil_amount", tileWok.getOilAmount()));
            }
        }
        else if (mode == ProbeMode.EXTENDED || mode == ProbeMode.DEBUG)
        {
            if (blockState.getBlock() == CuisineRegistry.EARTHEN_BASIN || blockState.getBlock() == CuisineRegistry.EARTHEN_BASIN_COLORED)
            {
                TileEntity tile = world.getTileEntity(data.getPos());
                if (tile instanceof TileBasinHeatable)
                {
                    TileBasinHeatable tileBasinHeatable = (TileBasinHeatable) tile;
                    MessageFormat formatter = new MessageFormat(I18n.translateToLocal(Cuisine.MODID + ".gui.progress"), MinecraftForgeClient.getLocale());
                    if (tileBasinHeatable.isWorking())
                    {
                        int max = tileBasinHeatable.getMaxHeatingTick();
                        probeInfo.text(TextStyleClass.INFO + formatter.format(new Object[] { 2 })); // "Using TheOneProbe"
                        probeInfo.progress(max - tileBasinHeatable.getCurrentHeatingTick(), max, new ProgressStyle().showText(false));
                    }
                    else
                    {
                        probeInfo.text(TextStyleClass.INFO + formatter.format(new Object[] { -1 })); // "suspended"
                    }
                }
            }
        }

    }

}
