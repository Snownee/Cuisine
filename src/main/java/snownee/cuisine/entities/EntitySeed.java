package snownee.cuisine.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import snownee.cuisine.Cuisine;
import snownee.cuisine.util.StacksUtil;

public class EntitySeed extends EntityThrowable
{
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack>createKey(EntityPotion.class, DataSerializers.ITEM_STACK);

    public EntitySeed(World worldIn)
    {
        super(worldIn);
    }

    public EntitySeed(World worldIn, double x, double y, double z, ItemStack stack)
    {
        super(worldIn, x, y, z);
        setItem(stack);
    }

    public EntitySeed(World worldIn, EntityLivingBase throwerIn, ItemStack stack)
    {
        super(worldIn, throwerIn);
        setItem(stack);
    }

    protected void entityInit()
    {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    protected float getGravityVelocity()
    {
        return 0.05F;
    }

    public void setItem(ItemStack stack)
    {
        this.getDataManager().set(ITEM, stack);
        this.getDataManager().setDirty(ITEM);
    }

    public ItemStack getItem()
    {
        ItemStack itemstack = this.getDataManager().get(ITEM);

        if (itemstack.isEmpty())
        {
            if (this.world != null)
            {
                Cuisine.logger.error("ThrownPotion entity {} has no item?!", this.getEntityId());
            }

            return new ItemStack(Items.WHEAT_SEEDS);
        }
        else
        {
            return itemstack;
        }
    }

    public static void registerFixesPotion(DataFixer fixer)
    {
        EntityThrowable.registerFixesThrowable(fixer, "Item");
        fixer.registerWalker(FixTypes.ENTITY, new ItemStackData(EntityPotion.class, "Item"));
    }

    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        ItemStack itemstack = new ItemStack(compound.getCompoundTag("Item"));

        if (itemstack.isEmpty())
        {
            this.setDead();
        }
        else
        {
            this.setItem(itemstack);
        }
    }

    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        ItemStack itemstack = this.getItem();

        if (!itemstack.isEmpty())
        {
            compound.setTag("Item", itemstack.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (result.entityHit != null)
        {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 4);
        }
        else if (!world.isRemote && getThrower() != null && getThrower() instanceof EntityPlayer && !((EntityPlayer) getThrower()).capabilities.isCreativeMode && result.typeOfHit == Type.BLOCK)
        {
            StacksUtil.spawnItemStack(world, result.hitVec.x, result.hitVec.y, result.hitVec.z, getItem(), true);
        }
        if (!world.isRemote)
        {
            setDead();
        }
    }

}
