package snownee.cuisine.api;

import java.util.Locale;

public enum CulinarySkillPoint
{
    PROFICIENCY, EXPERTISE;

    @Override
    public String toString()
    {
        // Why not Locale.ROOT? Google "Turkish test" and you will understand.
        // TL;DR: you want the English upper-lower letter mapping, not French
        // or Spanish or Turkish or something else.
        return super.toString().toLowerCase(Locale.ENGLISH);
    }
}
