package to.joe.bungee.commands;

import to.joe.bungee.Util;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.ChatColor;
import net.md_5.bungee.Permission;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.command.Command;
import net.md_5.bungee.command.CommandSender;

public class CommandAlert extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (this.getPermission(sender) != Permission.ADMIN) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "You must supply a message.");
        } else {
            final String message = ChatColor.translateAlternateColorCodes('&', Util.combineSplit(0, args, " "));
            final String normal = "[" + ChatColor.BLUE + "ALERT" + ChatColor.RESET + "] " + message;
            final String admin = "[" + ChatColor.BLUE + sender.getName() + ChatColor.RESET + "] " + message;
            for (final UserConnection con : BungeeCord.instance.connections.values()) {
                con.sendMessage(this.getPermission(con) == Permission.DEFAULT ? normal : admin);
            }
        }
    }
}
