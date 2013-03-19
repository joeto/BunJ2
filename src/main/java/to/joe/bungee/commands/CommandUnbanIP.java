package to.joe.bungee.commands;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import to.joe.bungee.SQLHandler;
import to.joe.bungee.Util;

public class CommandUnbanIP extends Command {

    public CommandUnbanIP() {
        super("unbanip", "j2.admin", "unban-ip", "ipunban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
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
                        for (final ProxiedPlayer con : ProxyServer.getInstance().getPlayers()) {
                            if (con.hasPermission("j2.admin")) {
                                con.sendMessage(message);
                            }
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                        for (final ProxiedPlayer con : ProxyServer.getInstance().getPlayers()) {
                            if (con.hasPermission("j2.admin")) {
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