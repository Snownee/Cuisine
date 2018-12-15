package snownee.cuisine.plugins.hwyla;

import java.text.MessageFormat;
import java.util.List;

import javax.annotation.Nonnull;

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
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.tiles.TileBasinHeatable;
import snownee.cuisine.util.I18nUtil;

final class CuisineBasinProvider implements IWailaDataProvider
{
    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getTileEntity() instanceof TileBasinHeatable)
        {
            TileBasinHeatable tile = (TileBasinHeatable) accessor.getTileEntity();
            NBTTagCompound data = accessor.getNBTData();
            boolean working = data.getBoolean("working");
            MessageFormat formatter = new MessageFormat(I18nUtil.translate("gui.progress"), MinecraftForgeClient.getLocale());
            if (working)
            {
                FluidStack fluidContent = FluidStack.loadFluidStackFromNBT(data.getCompoundTag("fluidContent"));
                if (fluidContent != null)
                {
                    tooltip.add(TextFormatting.GRAY + I18nUtil.translate("gui.fluid_show", fluidContent.getLocalizedName(), fluidContent.amount));
                }
                int currentProgress = data.getInteger("heatValue");
                int max = tile.getMaxHeatingTick();
                tooltip.add(TextFormatting.GRAY + formatter.format(new Object[] { 1 - (double)currentProgress / max }));
            }
            else
            {
                tooltip.add(TextFormatting.GRAY + formatter.format(new Object[] { -1 }));
            }
        }
        return tooltip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
    {
        if (te instanceof TileBasinHeatable)
        {
            FluidStack fluid = ((TileBasinHeatable) te).getCurrentFluidContent();
            if (fluid != null)
            {
                tag.setTag("fluidContent", fluid.writeToNBT(new NBTTagCompound()));
            }
            tag.setInteger("heatValue", ((TileBasinHeatable) te).getCurrentHeatingTick());
            tag.setBoolean("working", ((TileBasinHeatable) te).isWorking());
        }
        return tag;
    }
}
