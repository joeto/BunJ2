package to.joe.bungee.commands;

import to.joe.bungee.BunJ2;
import to.joe.bungee.Util;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.ChatColor;
import net.md_5.bungee.Permission;
import net.md_5.bungee.command.Command;
import net.md_5.bungee.command.CommandSender;

/**
 * Command to set a temp copy of the motd in real-time without stopping the
 * proxy.
 */
public class CommandMotd extends Command {

    private final BunJ2 j2;

    public CommandMotd(BunJ2 j2) {
        this.j2 = j2;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (this.getPermission(sender) != Permission.ADMIN) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
        } else {
            if (args.length > 0) {
                BungeeCord.instance.config.motd = ChatColor.translateAlternateColorCodes('&', Util.combineSplit(0, args, " "));
            } else {
                BungeeCord.instance.config.motd = this.j2.normalMotd;
            }
            sender.sendMessage("MOTD: " + BungeeCord.instance.config.motd);
        }
    }
}
