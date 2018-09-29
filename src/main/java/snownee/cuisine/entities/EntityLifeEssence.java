package snownee.cuisine.entities;

import net.minecraft.block.BlockTallGrass;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import snownee.cuisine.items.ItemLifeEssence;
import snownee.cuisine.network.PacketCustomEvent;
import snownee.kiwi.network.NetworkChannel;

public class EntityLifeEssence extends Entity
{
    private static final DataParameter<BlockPos> POS = EntityDataManager.createKey(EntityLifeEssence.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Integer> AGE = EntityDataManager.createKey(EntityLifeEssence.class, DataSerializers.VARINT);
    private EnumInvokeType type;
    private int age;
    private BlockPos pos;

    public EntityLifeEssence(World worldIn)
    {
        super(worldIn);
        setInvisible(true);
        setSize(0.001F, 0.001F);
    }

    public EntityLifeEssence(World worldIn, BlockPos pos, EnumInvokeType type)
    {
        this(worldIn);
        this.type = type;
        this.pos = pos;
        setAge(type == EnumInvokeType.GARDEN ? 100 : 400);
        setPos(pos);
        setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    }

    @Override
    public void onUpdate()
    {
        if (!world.isRemote && age % 10 == 0 && !checkBlock())
        {
            setDead();
        }
        if (--age <= 0)
        {
            if (world.isRemote)
            {
                ItemLifeEssence.splashParticles(world, new Vec3d(pos).add(0D, -0.5D, 0D));
            }
            else
            {
                if (type == EnumInvokeType.GARDEN)
                {
                    // world.setBlockState(pos, CuisineRegistry.GARDEN.getDefaultState(), 3);
                }
            }
            setDead();
        }
        else if (world.isRemote)
        {
            NetworkChannel.INSTANCE.sendToDimension(new PacketCustomEvent(6, (float) this.posX - 0.5F, (float) this.posY - 0.5F, (float) this.posZ - 0.5F), this.world.provider.getDimension());
        }
        else if (type == EnumInvokeType.FLOWER)
        {
            int step = 399 - age;
            int note = -1;
            switch (step)
            {
            case 0:
            case 12:
            case 48:
            case 60:
            case 96:
            case 108:
                note = 1;
                break;

            case 6:
            case 54:
            case 102:
                note = 3;
                break;

            case 18:
            case 66:
            case 114:
                note = 13;
                break;

            case 30:
            case 78:
            case 126:
            case 162:
                note = 10;
                break;

            case 138:
            case 168:
                note = 8;
                break;

            case 150:
                note = 6;
                break;

            case 192:
            case 204:
            case 240:
            case 252:
            case 288:
            case 300:
                note = 1;
                break;

            case 198:
            case 246:
            case 294:
                note = 3;
                break;

            case 210:
            case 258:
            case 340:
                note = 13;
                break;

            case 222:
            case 270:
            case 306:
            case 330:
            case 344:
                note = 10;
                break;

            case 336:
            case 354:
                note = 8;
                break;

            case 360:
                note = 6;
                break;
            }

            if (note != -1)
            {
                BlockPos blockPos = findLandingPlace();
                world.spawnEntity(new EntityFallingFlower(world, blockPos.getX() + 0.5D, blockPos.getY() + 50.5D, blockPos.getZ() + 0.5D, SoundEvents.BLOCK_NOTE_HARP, (byte) note));
            }
        }

    }

    private boolean[][] map;

    @SuppressWarnings("null")
    private BlockPos findLandingPlace()
    {
        if (map == null)
        {
            map = new boolean[31][31];
        }
        BlockPos retPos = null;
        int offsetX = 0, offsetZ = 0;
        int i;
        for (i = 0; i < 30; i++)
        {
            offsetX = (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(i % 15 + 5) + 1);
            offsetZ = (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(i % 15 + 5) + 1);
            if (map[15 + offsetX][15 + offsetZ])
            {
                continue;
            }
            retPos = world.getPrecipitationHeight(pos.add(offsetX, 0, offsetZ));
            if (Math.abs(retPos.getY() - pos.getY()) < 10 && (i >= 15 || Blocks.RED_FLOWER.canBlockStay(world, retPos, Blocks.RED_FLOWER.getDefaultState())))
            {
                break;
            }
        }
        map[15 + offsetX][15 + offsetZ] = true;
        return i == 30 ? new BlockPos(retPos.getX(), pos.getY(), retPos.getZ()) : retPos.up();
    }

    private boolean checkBlock()
    {
        switch (type)
        {
        case FLOWER:
            return true;
        case GARDEN:
            return world.getBlockState(pos).getBlock() instanceof BlockTallGrass;
        }
        return false;
    }

    @Override
    protected void entityInit()
    {
        dataManager.register(AGE, 100);
        dataManager.register(POS, new BlockPos(this));
    }

    protected void setAge(int age)
    {
        dataManager.set(AGE, age);
        this.age = age;
    }

    protected void setPos(BlockPos pos)
    {
        dataManager.set(POS, pos);
        this.pos = pos;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        if (AGE.equals(key))
        {
            this.age = dataManager.get(AGE);
        }
        else if (POS.equals(key))
        {
            this.pos = dataManager.get(POS);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        this.type = EnumInvokeType.values()[compound.getShort("Type")];
        this.age = compound.getShort("Age");
        this.pos = new BlockPos(compound.getInteger("TargetX"), compound.getInteger("TargetY"), compound.getInteger("TargetZ"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setShort("Type", (short) this.type.ordinal());
        compound.setShort("Age", (short) this.age);
        compound.setInteger("TargetX", pos.getX());
        compound.setInteger("TargetY", pos.getY());
        compound.setInteger("TargetZ", pos.getZ());
    }

    public enum EnumInvokeType
    {
        GARDEN, FLOWER
    }

}
