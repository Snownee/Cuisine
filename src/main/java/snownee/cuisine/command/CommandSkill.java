package snownee.cuisine.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinarySkillPoint;
import snownee.cuisine.api.CulinarySkillPointContainer;
import snownee.cuisine.api.util.SkillUtil;
import snownee.cuisine.util.I18nUtil;

public class CommandSkill extends CommandBase
{
    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getName()
    {
        return "culinaryskill";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return I18nUtil.getFullKey("command.culinaryskill");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (!((args.length == 3 && (args[1].equalsIgnoreCase("get") || args[1].equalsIgnoreCase("getLevel"))) || (args.length == 4 && (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("setLevel") || args[1].equalsIgnoreCase("addLevel")))))
        {
            throw new SyntaxErrorException(getUsage(sender));
        }
        EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(args[0]);
        if (player == null)
        {
            throw new EntityNotFoundException(args[0]);
        }
        CulinarySkillPointContainer cap = player.getCapability(CulinaryCapabilities.CULINARY_SKILL, null);
        if (cap == null || cap.getAvailableSkillPoints().isEmpty())
        {
            throw new EntityNotFoundException("Player does not have available skill.");
        }
        CulinarySkillPoint skillPoint = null;
        args[2] = args[2].toLowerCase(Locale.ROOT);
        for (CulinarySkillPoint point : cap.getAvailableSkillPoints())
        {
            if (point.toString().equals(args[2]))
            {
                skillPoint = point;
                break;
            }
        }
        if (skillPoint == null)
        {
            throw new SyntaxErrorException("Unknown skill point.");
        }
        try
        {
            switch (args[1].toLowerCase(Locale.ROOT))
            {
            case "set":
                SkillUtil.setPoint(player, skillPoint, Integer.parseInt(args[3]));
                break;
            case "setlevel":
                SkillUtil.setLevel(player, skillPoint, Integer.parseInt(args[3]));
                break;
            case "add":
                SkillUtil.increasePoint(player, skillPoint, Integer.parseInt(args[3]));
                break;
            case "addlevel":
                SkillUtil.increaseLevel(player, skillPoint, Integer.parseInt(args[3]));
                break;
            case "get":
                sender.sendMessage(new TextComponentTranslation("" + SkillUtil.getPoint(player, skillPoint))); // TODO: i18n
                return;
            case "getlevel":
                sender.sendMessage(new TextComponentTranslation("" + SkillUtil.getLevel(player, skillPoint))); // TODO: i18n
                return;
            default:
                break;
            }
            sender.sendMessage(new TextComponentTranslation("Success."));
        }
        catch (IllegalArgumentException e)
        {
            throw new SyntaxErrorException("Illegal skill value");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        else if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, Arrays.asList("get", "getLevel", "set", "setLevel", "add", "addLevel"));
        }
        else if (args.length == 3)
        {
            EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(args[0]);
            if (player != null)
            {
                CulinarySkillPointContainer cap = player.getCapability(CulinaryCapabilities.CULINARY_SKILL, null);
                if (cap != null)
                {
                    return getListOfStringsMatchingLastWord(args, cap.getAvailableSkillPoints().stream().map(CulinarySkillPoint::toString).collect(Collectors.toList()));
                }
            }
        }
        return Collections.emptyList();
    }
}
