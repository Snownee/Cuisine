package snownee.cuisine.api.prefab;

import com.google.common.collect.ImmutableList;

import snownee.cuisine.api.CulinarySkillPoint;
import snownee.cuisine.api.CulinarySkillPointContainer;

/**
 * Simplest implementation of {@link CulinarySkillPointContainer}. It does not include
 * any persistence support; to handle serialization and deserialization,
 * either extends this and implement {@code INbtSerializable}, or
 * re-implement on your own.
 */
public class SimpleCulinarySkillPointContainerImpl implements CulinarySkillPointContainer
{

    private int proficiency = 0;
    private int expertise = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSkillPoint(CulinarySkillPoint skillPoint)
    {
        if (skillPoint == CulinarySkillPoint.EXPERTISE)
        {
            return expertise;
        }
        else if (skillPoint == CulinarySkillPoint.PROFICIENCY)
        {
            return proficiency;
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setSkillPoint(CulinarySkillPoint skillPoint, int newValue)
    {
        if (skillPoint == CulinarySkillPoint.EXPERTISE)
        {
            expertise = newValue;
            return true;
        }
        else if (skillPoint == CulinarySkillPoint.PROFICIENCY)
        {
            proficiency = newValue;
            return true;
        }
        return false;
    }

    @Override
    public ImmutableList<CulinarySkillPoint> getAvailableSkillPoints()
    {
        return ImmutableList.of(CulinarySkillPoint.EXPERTISE, CulinarySkillPoint.PROFICIENCY);
    }
}
