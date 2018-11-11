package snownee.cuisine.internal.capabilities;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinarySkillPoint;
import snownee.cuisine.api.CulinarySkillPointContainer;
import snownee.cuisine.api.prefab.SimpleCulinarySkillPointContainerImpl;

public final class CulinarySkillCapability
{

    private CulinarySkillCapability()
    {
        throw new UnsupportedOperationException();
    }

    public static void init()
    {
        CapabilityManager.INSTANCE.register(CulinarySkillPointContainer.class, new Storage(), SimpleCulinarySkillPointContainerImpl::new);
        MinecraftForge.EVENT_BUS.register(EventListener.class);
    }

    static final class EventListener
    {
        private EventListener()
        {
            throw new UnsupportedOperationException("No instance for you");
        }

        @SubscribeEvent
        public static void onPlayerGatherCapabilities(AttachCapabilitiesEvent<Entity> event)
        {
            if (event.getObject() instanceof EntityPlayer)
            {
                if (!(event.getObject() instanceof FakePlayer)) // Filter FakePlayer
                {
                    event.addCapability(new ResourceLocation(Cuisine.MODID, "culinary_skill"), new DefaultProvider());
                }
            }
        }

        @SubscribeEvent
        public static void reSyncDataOnPlayerDeath(PlayerEvent.Clone event)
        {
            CulinarySkillPointContainer skill = event.getEntityPlayer().getCapability(CulinaryCapabilities.CULINARY_SKILL, null);
            if (skill != null)
            {
                CulinarySkillPointContainer original = event.getOriginal().getCapability(CulinaryCapabilities.CULINARY_SKILL, null);
                if (original != null)
                {
                    if (event.isWasDeath() && CuisineConfig.HARDCORE.enable && CuisineConfig.HARDCORE.loseSkillPointsOnDeath)
                    {
                        for (CulinarySkillPoint p : original.getAvailableSkillPoints())
                        {
                            int syncedPoints = original.getSkillPoint(p);
                            // Lose a portion of skill points when player died and corresponding
                            // config is enabled.
                            // This feature is disabled by default due to its hardcore nature.
                            syncedPoints *= CuisineConfig.HARDCORE.skillPointsRetainRatio;
                            skill.setSkillPoint(p, syncedPoints);
                        }
                    }
                }
            }
        }
    }

    public static class DefaultProvider extends SimpleCulinarySkillPointContainerImpl
            implements ICapabilitySerializable<NBTTagCompound>
    {

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CulinaryCapabilities.CULINARY_SKILL;
        }

        @Nullable
        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
        {
            if (capability == CulinaryCapabilities.CULINARY_SKILL)
            {
                return CulinaryCapabilities.CULINARY_SKILL.cast(this);
            }
            else
            {
                return null;
            }
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound data = new NBTTagCompound();
            for (CulinarySkillPoint skillPoint : getAvailableSkillPoints())
            {
                data.setInteger(skillPoint.toString(), this.getSkillPoint(skillPoint));
            }
            return data;
        }

        @Override
        public void deserializeNBT(NBTTagCompound data)
        {
            for (CulinarySkillPoint skillPoint : getAvailableSkillPoints())
            {
                this.setSkillPoint(skillPoint, data.getInteger(skillPoint.toString()));
            }
        }
    }

    static class Storage implements Capability.IStorage<CulinarySkillPointContainer>
    {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<CulinarySkillPointContainer> capability, CulinarySkillPointContainer instance, EnumFacing side)
        {
            NBTTagCompound data = new NBTTagCompound();
            for (CulinarySkillPoint skillPoint : instance.getAvailableSkillPoints())
            {
                data.setInteger(skillPoint.toString(), instance.getSkillPoint(skillPoint));
            }
            return data;
        }

        @Override
        public void readNBT(Capability<CulinarySkillPointContainer> capability, CulinarySkillPointContainer instance, EnumFacing side, NBTBase data)
        {
            if (data instanceof NBTTagCompound)
            {
                for (CulinarySkillPoint skillPoint : instance.getAvailableSkillPoints())
                {
                    instance.setSkillPoint(skillPoint, ((NBTTagCompound) data).getInteger(skillPoint.toString()));
                }
            }
        }
    }

}
