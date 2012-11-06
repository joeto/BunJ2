package to.joe.bungee.commands;

import java.util.Collection;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.ChatColor;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.command.Command;
import net.md_5.bungee.command.CommandSender;

/**
 * Command to list all players connected to the proxy.
 */
public class CommandList extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) {
        final StringBuilder users = new StringBuilder();
        final Collection<UserConnection> connections = BungeeCord.instance.connections.values();
        for (final UserConnection con : connections) {
            users.append(con.username);
            users.append(", ");
        }
        users.setLength(users.length() - 2);
        sender.sendMessage(ChatColor.BLUE + "Currently online across all servers (" + connections.size() + "): " + ChatColor.RESET + users);
    }
}
