package snownee.cuisine.api.prefab;

import snownee.cuisine.api.CulinarySkill;
import snownee.cuisine.api.CulinarySkillPoint;

public class SimpleCulinarySkillImpl implements CulinarySkill
{
    private final String name;
    private final CulinarySkillPoint skillPoint;
    private final int defaultLevelRequirement;

    public SimpleCulinarySkillImpl(String name, CulinarySkillPoint skillPoint, int defaultLevelRequirement)
    {
        this.name = name;
        this.skillPoint = skillPoint;
        this.defaultLevelRequirement = defaultLevelRequirement;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public int defaultLevelRequirement()
    {
        return defaultLevelRequirement;
    }

    @Override
    public CulinarySkillPoint skillPointRequirement()
    {
        return skillPoint;
    }

    @Override
    public String getTranslationKey()
    {
        return "cuisine.skill." + name;
    }

}
