package snownee.cuisine.plugins.hwyla;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import snownee.cuisine.Cuisine;
import snownee.cuisine.blocks.BlockCuisineCrops;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("deprecation")
public class CuisineCropBodyInfoProvider implements IWailaDataProvider
{

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (config.getConfig(HWYLACompatibility.KEY_SHOW_CROP_MATURITY))
        {
            IBlockState target = accessor.getBlockState();
            if (accessor.getBlock() instanceof BlockCuisineCrops)
            {
                BlockCuisineCrops cropBlock = (BlockCuisineCrops) accessor.getBlock();
                int age = cropBlock.getAge(target, accessor.getWorld(), accessor.getPosition());
                int maxAge = cropBlock.getMaxAge();
                if (age == maxAge)
                {
                    tooltip.add(TextFormatting.GREEN + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.fully_grown"));
                }
                else
                {
                    tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.grown", TextFormatting.YELLOW + "" + (age * 100) / maxAge));
                }
            }
        }
        return tooltip;
    }
}
