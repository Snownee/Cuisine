package snownee.cuisine.tiles;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.tiles.TileWok.SeasoningInfo;
import snownee.kiwi.network.PacketMod;

public class PacketWokSeasoningsUpdate implements PacketMod
{

    private BlockPos pos;
    private SeasoningInfo seasoningInfo;

    public PacketWokSeasoningsUpdate()
    {
    }

    PacketWokSeasoningsUpdate(BlockPos pos, SeasoningInfo seasoningInfo)
    {
        this.pos = pos;
        this.seasoningInfo = seasoningInfo;
    }

    @Override
    public void readDataFrom(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        seasoningInfo = new SeasoningInfo();
        seasoningInfo.volume = buf.readInt();
        if (seasoningInfo.volume > 0)
        {
            seasoningInfo.color = buf.readInt();
        }
    }

    @Override
    public void writeDataTo(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        buf.writeInt(seasoningInfo.volume);
        if (seasoningInfo.volume > 0)
        {
            buf.writeInt(seasoningInfo.color);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(EntityPlayerSP player)
    {
        TileEntity tile = null;
        if (player.world.isBlockLoaded(pos))
        {
            tile = player.world.getTileEntity(pos);
        }
        if (tile instanceof TileWok) // It is false if: 1. the area is not loaded or 2. target has no tile 3. we somehow got a tile mismatch.
        {
            ((TileWok) tile).seasoningInfo = seasoningInfo;
        }
    }

    @Override
    public void handleServer(EntityPlayerMP player)
    {
    }

}
