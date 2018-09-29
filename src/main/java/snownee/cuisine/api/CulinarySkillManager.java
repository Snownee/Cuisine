package snownee.cuisine.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mcp.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
public final class CulinarySkillManager
{
    private Map<CulinarySkill, Integer> mapSkillToLevelRequirement = new HashMap<>();
    private static CulinarySkillManager INSTANCE;

    public static CulinarySkillManager instance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new CulinarySkillManager();
        }
        return INSTANCE;
    }

    public static CulinarySkill register(CulinarySkill type)
    {
        if (instance().mapSkillToLevelRequirement.containsKey(type))
        {
            throw new IllegalArgumentException("Already registered");
        }
        instance().mapSkillToLevelRequirement.put(type, type.defaultLevelRequirement());
        return type;
    }

    public static int getLevelRequirement(CulinarySkill skill)
    {
        return instance().mapSkillToLevelRequirement.getOrDefault(skill, 0);
    }

    public static Set<CulinarySkill> getSkills()
    {
        return Collections.unmodifiableSet(instance().mapSkillToLevelRequirement.keySet());
    }
}
