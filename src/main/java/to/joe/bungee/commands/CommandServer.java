package to.joe.bungee.commands;

import java.util.Collection;

import to.joe.bungee.BunJ2;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.ChatColor;
import net.md_5.bungee.Permission;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.command.Command;
import net.md_5.bungee.command.CommandSender;

/**
 * Command to list and switch a player between available servers.
 */
public class CommandServer extends Command {

    private final BunJ2 j2;

    public CommandServer(BunJ2 j2) {
        this.j2 = j2;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof UserConnection)) {
            return;
        }
        final UserConnection con = (UserConnection) sender;
        final Collection<String> servers = BungeeCord.instance.config.servers.keySet();
        final boolean admin = this.getPermission(sender) != Permission.DEFAULT;
        if (args.length <= 0) {
            final StringBuilder serverList = new StringBuilder();
            for (final String server : servers) {
                if (!admin && this.j2.getConf().adminonlyservers.contains(server)) {
                    continue;
                }
                serverList.append(server);
                serverList.append(", ");
            }
            serverList.setLength(serverList.length() - 2);
            con.sendMessage(ChatColor.GOLD + "Servers: " + serverList.toString());
            con.sendMessage(ChatColor.GOLD + "Join them with: /server " + ChatColor.YELLOW + "name");
        } else {
            final String server = args[0];
            if (!servers.contains(server) || (!admin && this.j2.getConf().adminonlyservers.contains(server))) {
                con.sendMessage(ChatColor.RED + "The specified server does not exist");
            } else {
                con.connect(server);
            }
        }
    }
}
