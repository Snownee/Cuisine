package snownee.cuisine.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.inventory.ContainerNameFood;
import snownee.kiwi.network.PacketMod;
import snownee.kiwi.util.NBTHelper;

public class PacketNameFood implements PacketMod
{
    private String name;

    public PacketNameFood()
    {
    }

    public PacketNameFood(String name)
    {
        this.name = name;
    }

    @Override
    public void writeDataTo(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, name);
    }

    @Override
    public void readDataFrom(ByteBuf buffer)
    {
        this.name = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(EntityPlayerSP player)
    {
    }

    @Override
    public void handleServer(EntityPlayerMP player)
    {
        if (player.openContainer instanceof ContainerNameFood)
        {
            ItemStack stack = player.openContainer.inventorySlots.get(0).getStack();
            NBTHelper.of(stack).setString("customName", name);
            player.closeScreen();
        }
    }
}
