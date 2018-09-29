package snownee.cuisine.command;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public final class CommandRegistry
{
    public static void registryCommands(FMLServerStartingEvent e)
    {
        e.registerServerCommand(new CommandSkill());
    }
}
