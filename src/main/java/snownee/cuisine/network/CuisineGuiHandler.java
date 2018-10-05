package snownee.cuisine.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import snownee.cuisine.client.gui.CuisineGUI;
import snownee.cuisine.client.gui.GuiManual;
import snownee.cuisine.client.gui.GuiNameFood;
import snownee.cuisine.inventory.ContainerNameFood;
import snownee.cuisine.tiles.TileWok;

import javax.annotation.Nullable;

public class CuisineGuiHandler implements IGuiHandler
{
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case CuisineGUI.NAME_FOOD:
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileWok)
                {
                    return new ContainerNameFood((TileWok) tile);
                }
                return null;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case CuisineGUI.MANUAL:
                ItemStack stack = player.inventory.getStackInSlot(x);
                return new GuiManual(x, stack);
            case CuisineGUI.NAME_FOOD:
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileWok)
                {
                    return new GuiNameFood((TileWok) tile);
                }
                return null;
            default:
                return null;
        }
    }
}
