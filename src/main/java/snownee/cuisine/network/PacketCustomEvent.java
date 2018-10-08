package snownee.cuisine.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
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
import snownee.cuisine.client.particle.ParticleGrowth;
import snownee.cuisine.items.ItemLifeEssence;
import snownee.cuisine.tiles.TileJar;
import snownee.cuisine.tiles.TileWok;
import snownee.kiwi.network.PacketMod;

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
        case 1:
        {
            ItemLifeEssence.splashParticles(Minecraft.getMinecraft().world, new Vec3d(posX, posY, posZ));
            break;
        }
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
            TileEntity te = world.getTileEntity(new BlockPos(posX, posY, posZ));
            if (!(te instanceof TileWok))
            {
                return;
            }
            ++((TileWok) te).actionCycle;
            for (int k = 0; k < 4; ++k)
            {
                double x = posX + 0.5D + world.rand.nextGaussian() * 0.2D;
                double z = posZ + 0.5D + world.rand.nextGaussian() * 0.2D;
                world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, x, posY + 0.25, z, 0D, 0.1D, 0D);
            }
            world.playSound(posX + 0.5D + world.rand.nextGaussian() * 0.2D, posY + 0.25, posZ + 0.5D + world.rand.nextGaussian() * 0.2D, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1F, 1F, true);
        }
        case 4:
        {
            // TODO: Ingredients put in wok
            break;
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
        case 6:
        {
            WorldClient world = Minecraft.getMinecraft().world;
            if (world.rand.nextInt(5) == 0)
            {
                double mx = world.rand.nextGaussian() * 0.05D;
                double my = world.rand.nextGaussian() * 0.01D + 0.3D;
                double mz = world.rand.nextGaussian() * 0.05D;
                ParticleGrowth particle = new ParticleGrowth(world, posX + 0.5D, posY, posZ + 0.5D, mx, my, mz);
                Minecraft.getMinecraft().effectRenderer.addEffect(particle);
            }
            break;
        }
        default:
        {
            Cuisine.logger.error("Undefined event: {} {}, {}, {} {}", event, posX, posY, posZ, extraData);
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
