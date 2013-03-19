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

public class CommandBanIP extends Command {

    public CommandBanIP() {
        super("banip", "j2.admin", "ipban", "ban-ip");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
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
                        for (final ProxiedPlayer con : ProxyServer.getInstance().getPlayers()) {
                            if (con.hasPermission("j2.admin")) {
                                con.sendMessage(message);
                            }
                        }
                    } catch (final SQLException e) {
                        e.printStackTrace();
                        for (final ProxiedPlayer con : ProxyServer.getInstance().getPlayers()) {
                            if (con.hasPermission("j2.admin")) {
                                con.sendMessage("Failed to ban " + target);
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