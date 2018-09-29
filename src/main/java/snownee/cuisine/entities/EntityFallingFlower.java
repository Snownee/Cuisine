package snownee.cuisine.entities;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.util.Random;
import snownee.cuisine.items.ItemLifeEssence;

public class EntityFallingFlower extends EntityFallingBlock implements IEntityAdditionalSpawnData
{
    private byte note;
    private SoundEvent instrument;
    private static final IBlockState[] STATES = new IBlockState[] { Blocks.YELLOW_FLOWER.getDefaultState(),
            Blocks.RED_FLOWER.getStateFromMeta(0), Blocks.RED_FLOWER.getStateFromMeta(1),
            Blocks.RED_FLOWER.getStateFromMeta(2), Blocks.RED_FLOWER.getStateFromMeta(3),
            Blocks.RED_FLOWER.getStateFromMeta(4), Blocks.RED_FLOWER.getStateFromMeta(5),
            Blocks.RED_FLOWER.getStateFromMeta(6), Blocks.RED_FLOWER.getStateFromMeta(7),
            Blocks.RED_FLOWER.getStateFromMeta(8) };

    public EntityFallingFlower(World worldIn)
    {
        super(worldIn);
    }

    public EntityFallingFlower(World worldIn, double x, double y, double z, SoundEvent instrument, byte note)
    {
        super(worldIn, x, y, z, STATES[new Random().nextInt(STATES.length)]);
        fallTime = 1;
        setNoGravity(true);
        motionY = -1.25D;
        this.instrument = instrument;
        this.note = note;
        ReflectionHelper.setPrivateValue(EntityFallingBlock.class, this, 0, "field_145815_h", "fallHurtMax");
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setByte("Note", note);
        compound.setString("Instrument", instrument.getRegistryName().toString());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.note = compound.getByte("Note");
        this.instrument = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(compound.getString("Instrument")));
    }

    @Override
    public void onUpdate()
    {
        if (getBlock() == null)
        {
            this.setDead();
            return;
        }
        super.onUpdate();
        if (onGround)
        {
            float f = (float) Math.pow(2.0D, (note - 12) / 12.0D);
            world.playSound(null, getPosition(), instrument, SoundCategory.NEUTRAL, 4.0F, f);

            world.setEntityState(this, (byte) 1);
            this.setDead();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleStatusUpdate(byte id)
    {
        if (id == 1) // Magic number 1 denotes "ready to do effect and being cleared out"
        {
            this.doEffect();
            this.setDead();
        }
    }

    @SideOnly(Side.CLIENT)
    private void doEffect()
    {
        ItemLifeEssence.splashParticles(world, getPosition());
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        ByteBufUtils.writeTag(buffer, NBTUtil.writeBlockState(new NBTTagCompound(), this.fallTile));
        buffer.writeByte(this.note);
        ByteBufUtils.writeRegistryEntry(buffer, instrument);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        fallTile = NBTUtil.readBlockState(ByteBufUtils.readTag(additionalData));
        this.note = additionalData.readByte();
        this.instrument = ByteBufUtils.readRegistryEntry(additionalData, ForgeRegistries.SOUND_EVENTS);
    }

}
