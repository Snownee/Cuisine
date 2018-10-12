package snownee.cuisine.plugins.hwyla;

import java.util.List;

import javax.annotation.Nonnull;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
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
            TileBasinHeatable tile = (TileBasinHeatable) accessor.getTileEntity();
            NBTTagCompound data = accessor.getNBTData();
            boolean working = data.getBoolean("working");
            if (working)
            {
                FluidStack fluidContent = FluidStack.loadFluidStackFromNBT(data.getCompoundTag("fluidContent"));
                if (fluidContent != null)
                {
                    tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.fluid_show", fluidContent.getLocalizedName(), fluidContent.amount));
                }
                tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.progress"));
                int currentProgress = data.getInteger("heatValue");
                int max = tile.getMaxHeatingTick();
                tooltip.add(SpecialChars.getRenderString("waila.progress", String.valueOf(max - currentProgress), String.valueOf(max)));
            }
            else
            {
                tooltip.add(TextFormatting.GRAY + I18n.translateToLocalFormatted(Cuisine.MODID + ".gui.suspended"));
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
