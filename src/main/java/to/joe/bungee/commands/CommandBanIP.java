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

public class CommandBanIP extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (this.getPermission(sender) == Permission.DEFAULT) {
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/banip [IP] [REASON]");
            return;
        }
        if (Util.isIP(args[0])) {
            final String reason = Util.combineSplit(1, args, " ");
            final String message = ChatColor.RED + "Banning " + args[0] + " by " + sender.getName() + ": " + reason;
            final String target = args[0];
            final String admin = sender.getName();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        SQLHandler.banIP(target, reason, admin);
                        for (final UserConnection con : BungeeCord.instance.connections.values()) {
                            if (CommandBanIP.this.getPermission(con) != Permission.DEFAULT) {
                                con.sendMessage(message);
                            }
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                        for (final UserConnection con : BungeeCord.instance.connections.values()) {
                            if (CommandBanIP.this.getPermission(con) != Permission.DEFAULT) {
                                con.sendMessage("Failed to ban " + target);
                            }
                        }
                    }
                }
            }, 1);
        } else {
            sender.sendMessage(net.md_5.bungee.ChatColor.RED + "Not a valid IP");
        }
    }
}
