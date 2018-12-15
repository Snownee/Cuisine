package snownee.cuisine.tiles;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.api.CulinaryHub;
import snownee.kiwi.network.PacketMod;

public class PacketIncrementalWokUpdate implements PacketMod
{

    private BlockPos pos;
    private ItemStack diff;

    public PacketIncrementalWokUpdate()
    {
        // No-op, for reading data
    }

    /**
     *
     * @param pos
     *            Position of TileEntity
     * @param diff
     *            The incremental update content
     */
    PacketIncrementalWokUpdate(BlockPos pos, ItemStack diff)
    {
        this.pos = pos;
        this.diff = diff;
    }

    @Override
    public void writeDataTo(ByteBuf buffer)
    {
        buffer.writeLong(pos.toLong());
        ByteBufUtils.writeItemStack(buffer, diff);
    }

    @Override
    public void readDataFrom(ByteBuf buffer)
    {
        this.pos = BlockPos.fromLong(buffer.readLong());
        this.diff = ByteBufUtils.readItemStack(buffer);
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
            if (CulinaryHub.API_INSTANCE.isKnownIngredient(diff))
            {
                ((TileWok) tile).ingredientsForRendering.add(diff);
                for (int k = 0; k < 4; ++k)
                {
                    double x = tile.getPos().getX() + 0.5D + tile.getWorld().rand.nextGaussian() * 0.2D;
                    double z = tile.getPos().getZ() + tile.getWorld().rand.nextGaussian() * 0.2D;
                    tile.getWorld().spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, x, tile.getPos().getY() + 0.25, z, 0D, 0.1D, 0D);
                }
                tile.getWorld().playSound(tile.getPos().getX() + 0.5D + tile.getWorld().rand.nextGaussian() * 0.2D, tile.getPos().getY() + 0.25, tile.getPos().getZ() + 0.5D + tile.getWorld().rand.nextGaussian() * 0.2D, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1F, 1F, true);
            }
            else // When it's empty or it's bad packet
            {
                ((TileWok) tile).ingredientsForRendering.clear();
                ((TileWok) tile).seasoningInfo = null;
            }
        }
    }

    @Override
    public void handleServer(EntityPlayerMP player)
    {
        // No-op
    }
}
