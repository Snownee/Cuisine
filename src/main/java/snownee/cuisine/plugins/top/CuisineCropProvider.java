package snownee.cuisine.plugins.top;

import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.config.Config;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import snownee.cuisine.Cuisine;
import snownee.cuisine.blocks.BlockCuisineCrops;

@SuppressWarnings("deprecation")
public class CuisineCropProvider implements IProbeInfoProvider
{

    @Override
    public String getID()
    {
        return Cuisine.MODID + ":crop";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
    {
        if (blockState.getBlock() instanceof BlockCuisineCrops)
        {
            if (Tools.show(mode, Config.getRealConfig().getShowCropPercentage()))
            {
                BlockCuisineCrops crops = (BlockCuisineCrops) blockState.getBlock();
                int age = crops.getAge(blockState, world, data.getPos());
                int maxAge = crops.getMaxAge();
                if (age == maxAge)
                {
                    probeInfo.text(TextStyleClass.OK + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.fully_grown"));
                }
                else
                {
                    probeInfo.text(TextStyleClass.LABEL + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.grown", TextStyleClass.WARNING + "" + (age * 100) / maxAge));
                }
            }
        }
    }

}
