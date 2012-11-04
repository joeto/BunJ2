package to.joe.bungee.commands;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.ChatColor;
import net.md_5.bungee.Permission;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.command.Command;
import net.md_5.bungee.command.CommandSender;

import to.joe.bungee.SQLHandler;

public class CommandIP extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (this.getPermission(sender) == Permission.DEFAULT) {
            return;
        }
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
                final UserConnection con = BungeeCord.instance.connections.get(senderName);
                if (con != null) {
                    for (final String line : list) {
                        con.sendMessage(line);
                    }
                }
            }

        }, 1);
    }
}
