package snownee.cuisine.plugins.hwyla;

import java.util.List;

import javax.annotation.Nonnull;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import snownee.cuisine.blocks.BlockCuisineCrops;
import snownee.cuisine.blocks.BlockModLeaves;
import snownee.cuisine.blocks.BlockShearedLeaves;
import snownee.cuisine.util.I18nUtil;

final class CuisineCropProvider implements IWailaDataProvider
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
                    tooltip.add(TextFormatting.GREEN + I18nUtil.translate("gui.fully_grown"));
                }
                else
                {
                    tooltip.add(TextFormatting.GRAY + I18nUtil.translate("gui.grown", TextFormatting.YELLOW + "" + (age * 100) / maxAge));
                }
            }
            else if (accessor.getBlock() instanceof BlockModLeaves)
            {
                tooltip.add(I18nUtil.translate("gui.leaves." + accessor.getBlockState().getValue(BlockModLeaves.AGE)));
            }
            else if (accessor.getBlock() instanceof BlockShearedLeaves)
            {
                tooltip.add(I18nUtil.translate("gui.leaves.0"));
            }
        }
        return tooltip;
    }
}
