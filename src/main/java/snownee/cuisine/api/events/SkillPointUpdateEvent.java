package snownee.cuisine.api.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import snownee.cuisine.api.CulinarySkillPoint;

@Cancelable
public class SkillPointUpdateEvent extends PlayerEvent
{
    private CulinarySkillPoint skillPoint;
    private int newValue;

    public SkillPointUpdateEvent(EntityPlayer player, CulinarySkillPoint skillPoint, int newValue)
    {
        super(player);
        this.skillPoint = skillPoint;
        this.newValue = newValue;
    }

    public CulinarySkillPoint getSkillPoint()
    {
        return skillPoint;
    }

    public void setSkillPoint(CulinarySkillPoint skillPoint)
    {
        this.skillPoint = skillPoint;
    }

    public int getNewValue()
    {
        return newValue;
    }

    public void setNewValue(int newValue)
    {
        this.newValue = newValue;
    }

}
