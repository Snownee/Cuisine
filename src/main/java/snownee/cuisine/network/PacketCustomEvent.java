package snownee.cuisine.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.api.IHeatHandler;
import snownee.cuisine.tiles.TileJar;
import snownee.cuisine.tiles.heat.FuelHeatHandler;
import snownee.cuisine.tiles.utensils.TileWok;
import snownee.kiwi.network.PacketMod;

import java.util.Random;

public class PacketCustomEvent implements PacketMod
{

    // TODO (3TUSK): Can we somehow flatten this?

    private short event;
    private float posX;
    private float posY;
    private float posZ;
    private int extraData = 0;

    public PacketCustomEvent()
    {
    }

    public PacketCustomEvent(int event, Vec3d particlePos)
    {
        this(event, (float) particlePos.x, (float) particlePos.y, (float) particlePos.z);
    }

    public PacketCustomEvent(int event, float x, float y, float z)
    {
        this.event = (short) event;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    public PacketCustomEvent(int event, BlockPos pos)
    {
        this(event, pos.getX(), pos.getY(), pos.getZ());
    }

    public PacketCustomEvent(int event, BlockPos pos, int extra)
    {
        this(event, pos.getX(), pos.getY(), pos.getZ());
        this.extraData = extra;
    }

    @Override
    public void writeDataTo(ByteBuf buffer)
    {
        buffer.writeShort(event);
        buffer.writeFloat(posX);
        buffer.writeFloat(posY);
        buffer.writeFloat(posZ);
        buffer.writeInt(extraData);
    }

    @Override
    public void readDataFrom(ByteBuf buffer)
    {
        event = buffer.readShort();
        posX = buffer.readFloat();
        posY = buffer.readFloat();
        posZ = buffer.readFloat();
        extraData = buffer.readInt();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(EntityPlayerSP player)
    {
        switch (event)
        {
        case 2:
        {
            World world = Minecraft.getMinecraft().world;
            double x = posX + 0.5D + world.rand.nextGaussian() * 0.2D;
            double z = posZ + 0.5D + world.rand.nextGaussian() * 0.2D;
            world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, x, posY + 0.25D, z, 0.0D, 0.0D, 0.0D, Block.getStateId(world.getBlockState(new BlockPos(posX, posY, posZ))));
            break;
        }
        case 3:
        {
            // TODO Fine tuning on FX
            World world = Minecraft.getMinecraft().world;
            Random rand = world.rand;
            TileEntity te = world.getTileEntity(new BlockPos(posX, posY, posZ));
            if (te instanceof TileWok)
            {
                ++((TileWok) te).actionCycle;
                IHeatHandler handler = ((TileWok) te).getHeatHandler();
                if (handler instanceof FuelHeatHandler)
                {
                    int level = ((FuelHeatHandler) handler).getLevel();
                    for (int k = 0; k < level * 2; ++k)
                    {
                        double x = posX + 0.5D + rand.nextGaussian() * 0.2D;
                        double z = posZ + 0.5D + rand.nextGaussian() * 0.2D;
                        world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, x, posY + 0.25, z, 0D, 0.1D, 0D);
                    }
                    if (level > 0)
                    {
                        world.playSound(posX + 0.5, posY + 0.25, posZ + 0.5, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.4F + rand.nextFloat() * 0.2F * level, 0.7F + rand.nextFloat() * 0.1F * level, false);
                    }
                }
            }
        }
        case 5:
        {
            TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(new BlockPos(posX, posY, posZ));
            if (tile instanceof TileJar)
            {
                ((TileJar) tile).forceSetWorkingStatus(extraData == 1);
            }
            break;
        }
        default:
        {
            Cuisine.logger.warn("Undefined event: {} {}, {}, {} {}. Skipping", event, posX, posY, posZ, extraData);
            break;
        }
        }
    }

    @Override
    public void handleServer(EntityPlayerMP player)
    {
        // No-op
    }
}
