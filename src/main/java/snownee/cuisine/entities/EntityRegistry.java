package snownee.cuisine.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;

@Mod.EventBusSubscriber(modid = Cuisine.MODID)
public final class EntityRegistry
{

    @SubscribeEvent
    public static void onEntityRegister(RegistryEvent.Register<EntityEntry> event)
    {
        //        if (!CuisineConfig.GENERAL.disableEssence)
        //        {
        event.getRegistry().register(EntityEntryBuilder.<EntityFallingFlower>create().entity(EntityFallingFlower.class).id(new ResourceLocation(Cuisine.MODID, "falling_flower"), 0).name(Cuisine.MODID + ".falling_flower").tracker(160, 20, true).build());
        //        , EntityEntryBuilder.<EntityLifeEssence>create().entity(EntityLifeEssence.class).id(new ResourceLocation(Cuisine.MODID, "life_essence"), 1).name(Cuisine.MODID + ".life_essence").tracker(160, 20, false).build()
        //        }
    }

    private static boolean dispersalEffectImpl(EntityLivingBase entity)
    {
        return entity != null && !entity.isEntityUndead() && entity.getActivePotionEffect(CuisineRegistry.DISPERSAL) != null;
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event)
    {
        if (event.getWorld().isRemote || event.isCanceled())
        {
            return;
        }
        if (event.getEntity() instanceof EntityMob && ((EntityMob) event.getEntity()).isEntityUndead())
        {
            EntityMob mob = (EntityMob) event.getEntity();
            for (EntityAITaskEntry task : mob.tasks.taskEntries)
            {
                if (task.action instanceof EntityAIAvoidEntity)
                {
                    if (((EntityAIAvoidEntity) task.action).classToAvoid == EntityLivingBase.class)
                    {
                        return;
                    }
                }
            }
            mob.tasks.addTask(1, new EntityAIAvoidEntity<>(mob, EntityLivingBase.class, EntityRegistry::dispersalEffectImpl, 10.0F, 1.0D, 1.2D));
        }
        else if (event.getEntity() instanceof EntityArrow)
        {
            EntityArrow arrow = (EntityArrow) event.getEntity();
            if (arrow.shootingEntity instanceof EntityLivingBase)
            {
                setFire(arrow, (EntityLivingBase) arrow.shootingEntity, 100);
            }
        }
        else if (event.getEntity() instanceof EntityThrowable)
        {
            EntityThrowable throwable = (EntityThrowable) event.getEntity();
            if (throwable.getThrower() != null)
            {
                setFire(throwable, throwable.getThrower(), 100);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingDamage(LivingDamageEvent event)
    {
        if (event.isCanceled())
        {
            return;
        }
        DamageSource source = event.getSource();
        Entity entity = source.getTrueSource();
        if (!source.isProjectile() && !source.isMagicDamage() && entity instanceof EntityLivingBase)
        {
            setFire(event.getEntity(), (EntityLivingBase) entity, 3);
        }
    }

    private static void setFire(Entity projectile, EntityLivingBase shooter, int seconds)
    {
        PotionEffect effect = shooter.getActivePotionEffect(CuisineRegistry.HOT);
        if (effect != null)
        {
            float rate = 0.4F + effect.getAmplifier() * 0.2F;
            float random = shooter.getEntityWorld().rand.nextFloat();
            if (random < rate)
            {
                projectile.setFire(seconds);
                if (random < rate * rate * rate)
                {
                    shooter.setFire(3);
                }
            }
        }
    }

}
