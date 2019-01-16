package snownee.cuisine.network;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.client.gui.CuisineGUI;
import snownee.cuisine.client.gui.GuiManual;
import snownee.cuisine.client.gui.GuiNameFood;
import snownee.cuisine.inventory.ContainerNameFood;

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
            if (tile instanceof CookingVessel)
            {
                return new ContainerNameFood((CookingVessel) tile, world);
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
            return new GuiManual();
        case CuisineGUI.NAME_FOOD:
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof CookingVessel)
            {
                return new GuiNameFood((CookingVessel) tile, world);
            }
            return null;
        default:
            return null;
        }
    }
}
