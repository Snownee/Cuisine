package snownee.cuisine.api;

import com.google.common.collect.ImmutableList;

/**
 * The storage of culinary skill data of a certain target, usually
 * an entity.
 */
public interface CulinarySkillPointContainer
{

    /**
     * Retrieve the current culinary skill points.
     * @return the current skill points
     */
    int getSkillPoint(CulinarySkillPoint skillPoint);

    /**
     * Update the current culinary skill points to that of
     * {@code newValue}.
     * @param newValue the new skill points number
     */
    boolean setSkillPoint(CulinarySkillPoint skillPoint, int newValue);

    ImmutableList<CulinarySkillPoint> getAvailableSkillPoints();

}
