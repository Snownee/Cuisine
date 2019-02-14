package snownee.cuisine.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.events.SkillPointUpdateEvent;
import snownee.cuisine.api.util.SkillUtil;
import snownee.cuisine.library.CuisineFoodStats;
import snownee.cuisine.network.PacketSkillLevelIncreased;
import snownee.kiwi.network.NetworkChannel;

@EventBusSubscriber(modid = Cuisine.MODID)
public class PlayerHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerSkillPointUpdate(SkillPointUpdateEvent event)
    {
        int oldLevel = SkillUtil.getLevel(event.getEntityPlayer(), event.getSkillPoint());
        int newLevel = SkillUtil.getLevel(event.getNewValue());
        if (newLevel > oldLevel && !event.getEntityPlayer().getEntityWorld().isRemote)
        {
            NetworkChannel.INSTANCE.sendToPlayer(new PacketSkillLevelIncreased(event.getSkillPoint(), (short) oldLevel, (short) newLevel), (EntityPlayerMP) event.getEntityPlayer());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerJoinWorld(EntityJoinWorldEvent event)
    {
        if (CuisineConfig.HARDCORE.enable && CuisineConfig.HARDCORE.lowerFoodLevel && !Loader.isModLoaded("applecore"))
        {
            if (event.getEntity() instanceof EntityPlayer)
            {
                ((EntityPlayer) event.getEntity()).foodStats = new CuisineFoodStats();
            }
        }
    }

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event)
    {
        if (event.getEntityPlayer().isPotionActive(CuisineRegistry.COLD_BLOOD))
        {
            event.setDamageModifier(event.getDamageModifier() * 2);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onKnockBack(LivingKnockBackEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.onGround && entity.isSneaking() && entity.isPotionActive(CuisineRegistry.TOUGHNESS))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onProjectileImpact(LivingAttackEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.onGround && entity.isSneaking() && event.getSource().isProjectile() && entity.isPotionActive(CuisineRegistry.TOUGHNESS))
        {
            event.setCanceled(true);
        }
    }
}
