package snownee.cuisine.api;

import net.minecraft.entity.player.EntityPlayerMP;

public interface CulinarySkill
{
    /**
     * The string identifier of the skill
     *
     * @return name
     */
    String getName();

    /**
     * The minimum culinary skill level to unlock this skill
     *
     * @return level
     */
    int defaultLevelRequirement();

    CulinarySkillPoint skillPointRequirement();

    /**
     * The unlocalized name of the skill
     *
     * @return unlocalized name
     */
    String getTranslationKey();

    static void register(CulinarySkill type)
    {
        try
        {
            Class.forName("snownee.cuisine.internal.CulinarySkillManager").getMethod("register", CulinarySkill.class).invoke(null, type);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    static int getPlayerSkillLevel(EntityPlayerMP player, CulinarySkill type)
    {
        try
        {
            return (int) Class.forName("snownee.cuisine.util.CulinarySkillUtil").getMethod("getPlayerSkillLevel", EntityPlayerMP.class, String.class).invoke(null, player, type.getName());
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return 0;
    }
}
