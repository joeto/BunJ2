package to.joe.bungee.commands;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import to.joe.bungee.SQLHandler;

public class CommandIP extends Command {

    public CommandIP() {
        super("ip", "j2.admin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/ip [user]");
            return;
        }
        final String username = args[0];
        final String senderName = sender.getName();
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                final List<String> list = SQLHandler.iplookup(username);
                final ProxiedPlayer con = ProxyServer.getInstance().getPlayer(senderName);
                if (con != null) {
                    for (final String line : list) {
                        con.sendMessage(line);
                    }
                }
            }

        }, 1);
    }
}