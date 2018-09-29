package snownee.cuisine.network;

import java.util.Set;
import java.util.stream.Collectors;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.api.CulinarySkill;
import snownee.cuisine.api.CulinarySkillManager;
import snownee.cuisine.api.CulinarySkillPoint;
import snownee.cuisine.client.gui.SkillToast;
import snownee.kiwi.network.PacketMod;

public class PacketSkillLevelIncreased implements PacketMod
{
    private CulinarySkillPoint skillPoint;
    private short oldValue;
    private short newValue;

    public PacketSkillLevelIncreased()
    {
    }

    public PacketSkillLevelIncreased(CulinarySkillPoint skillPoint, short oldValue, short newValue)
    {
        this.skillPoint = skillPoint;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public void writeDataTo(ByteBuf buffer)
    {
        buffer.writeShort(skillPoint.ordinal());
        buffer.writeShort(oldValue);
        buffer.writeShort(newValue);
    }

    @Override
    public void readDataFrom(ByteBuf buffer)
    {
        CulinarySkillPoint[] values = CulinarySkillPoint.values();
        skillPoint = values[MathHelper.clamp(buffer.readShort(), 0, values.length - 1)];
        oldValue = buffer.readShort();
        newValue = buffer.readShort();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(EntityPlayerSP player)
    {
        if (newValue > oldValue)
        {
            player.world.playSound(player, player.posX, player.posY + player.eyeHeight, player.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, player.getSoundCategory(), 0.75F, 1);
            Set<CulinarySkill> skills = CulinarySkillManager.getSkills().stream().filter(skill -> skill.skillPointRequirement() == skillPoint && CulinarySkillManager.getLevelRequirement(skill) > oldValue && CulinarySkillManager.getLevelRequirement(skill) <= newValue).collect(Collectors.toSet());
            Minecraft.getMinecraft().getToastGui().add(new SkillToast(skillPoint, newValue, skills));
        }
    }

    @Override
    public void handleServer(EntityPlayerMP player)
    {
        // NO-OP
    }

}
