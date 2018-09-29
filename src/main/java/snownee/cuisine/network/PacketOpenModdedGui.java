package snownee.cuisine.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.kiwi.network.PacketMod;

public class PacketOpenModdedGui implements PacketMod
{
    private int id;
    private int x;
    private int y;
    private int z;

    public PacketOpenModdedGui()
    {
    }

    public PacketOpenModdedGui(int id, int x, int y, int z)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void writeDataTo(ByteBuf buffer)
    {
        buffer.writeInt(id);
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
    }

    @Override
    public void readDataFrom(ByteBuf buffer)
    {
        id = buffer.readInt();
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(EntityPlayerSP player)
    {
        player.openGui(Cuisine.getInstance(), id, player.world, x, y, z);
    }

    @Override
    public void handleServer(EntityPlayerMP player)
    {
    }

}
