package snownee.cuisine.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
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
        if (newLevel > oldLevel)
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
}
