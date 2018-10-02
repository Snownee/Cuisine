package snownee.cuisine.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import snownee.kiwi.network.PacketMod;

import java.util.List;

public class PacketSkillLevelResponse implements PacketMod
{
    List<String> skills;
    public PacketSkillLevelResponse(List<String> skills) {
        this.skills = skills;
    }

    @Override
    public void writeDataTo(ByteBuf byteBuf)
    {

    }

    @Override
    public void readDataFrom(ByteBuf byteBuf)
    {
    }

    @Override
    public void handleClient(EntityPlayerSP entityPlayerSP)
    {

    }

    @Override
    public void handleServer(EntityPlayerMP entityPlayerMP)
    {

    }
}
