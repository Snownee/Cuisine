package snownee.cuisine.api.util;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinarySkill;
import snownee.cuisine.api.CulinarySkillManager;
import snownee.cuisine.api.CulinarySkillPoint;
import snownee.cuisine.api.CulinarySkillPointContainer;
import snownee.cuisine.api.events.SkillPointUpdateEvent;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SkillUtil
{
    public static int getPoint(EntityPlayer player, CulinarySkillPoint skillPoint)
    {
        CulinarySkillPointContainer cap = player.getCapability(CulinaryCapabilities.CULINARY_SKILL, null);
        if (cap != null)
        {
            return cap.getSkillPoint(skillPoint);
        }
        return 0;
    }

    public static void setPoint(EntityPlayer player, CulinarySkillPoint skillPoint, int value)
    {
        if (value < 0)
        {
            throw new IllegalArgumentException("negative skill level");
        }
        CulinarySkillPointContainer cap = player.getCapability(CulinaryCapabilities.CULINARY_SKILL, null);
        if (cap != null)
        {
            SkillPointUpdateEvent event = new SkillPointUpdateEvent(player, skillPoint, value);
            MinecraftForge.EVENT_BUS.post(event);
            if (!event.isCanceled())
            {
                cap.setSkillPoint(event.getSkillPoint(), event.getNewValue());
            }
        }
    }

    public static int getLevel(EntityPlayer player, CulinarySkillPoint skillPoint)
    {
        return getLevel(getPoint(player, skillPoint));
    }

    public static int getLevel(int point)
    {
        return MathHelper.clamp(point / 50, 0, Short.MAX_VALUE);
    }

    public static void setLevel(EntityPlayer player, CulinarySkillPoint skillPoint, int level)
    {
        setPoint(player, skillPoint, level * 50);
    }

    public static void increasePoint(EntityPlayer player, CulinarySkillPoint skillPoint, int delta)
    {
        setPoint(player, skillPoint, getPoint(player, skillPoint) + delta);
    }

    /**
     * 增加玩家对技能等级
     *
     * @param player 玩家
     * @param skillPoint  技能
     * @param delta  提升等级
     */
    public static void increaseLevel(EntityPlayer player, CulinarySkillPoint skillPoint, int delta)
    {
        setLevel(player, skillPoint, getLevel(player, skillPoint) + delta);
    }

    public static boolean hasPlayerLearnedSkill(EntityPlayer player, CulinarySkill skill)
    {
        if (player instanceof FakePlayer)
        {
            return false;
        }
        int pointPlayer = getLevel(player, skill.skillPointRequirement());
        int pointReq = CulinarySkillManager.getLevelRequirement(skill);
        return pointPlayer >= pointReq;
    }

    public static List<String> getKnownSkillNames()
    {
        return CulinarySkillManager.getSkills().stream().map(CulinarySkill::getName).collect(Collectors.toList());
    }
}
