package to.joe.bungee.commands;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.ChatColor;
import net.md_5.bungee.Permission;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.command.Command;
import net.md_5.bungee.command.CommandSender;

import to.joe.bungee.SQLHandler;
import to.joe.bungee.Util;

public class CommandUnbanIP extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (this.getPermission(sender) == Permission.DEFAULT) {
            return;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/unbanip [user]");
            return;
        }
        if (Util.isIP(args[0])) {
            final String message = ChatColor.RED + "Unbanning " + args[0] + " by " + sender.getName();
            final String target = args[0];
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        SQLHandler.unbanIP(target);
                        for (final UserConnection con : BungeeCord.instance.connections.values()) {
                            if (CommandUnbanIP.this.getPermission(con) != Permission.DEFAULT) {
                                con.sendMessage(message);
                            }
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                        for (final UserConnection con : BungeeCord.instance.connections.values()) {
                            if (CommandUnbanIP.this.getPermission(con) != Permission.DEFAULT) {
                                con.sendMessage("Failed to unban ip for " + target);
                            }
                        }
                    }
                }
            }, 1);
        } else {
            sender.sendMessage(ChatColor.RED + "Not a valid IP");
        }
    }
}
