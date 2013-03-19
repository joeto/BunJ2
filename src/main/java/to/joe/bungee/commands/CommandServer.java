package to.joe.bungee.commands;

import java.util.Set;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Command to list and switch a player between available servers.
 */
public class CommandServer extends Command {

    public CommandServer() {
        super("server", "j2.default");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        final ProxiedPlayer con = (ProxiedPlayer) sender;
        final Set<String> servers = ProxyServer.getInstance().getServers().keySet();
        if (args.length <= 0) {
            final StringBuilder serverList = new StringBuilder();
            for (final String server : servers) {
                serverList.append(server);
                serverList.append(", ");
            }
            serverList.setLength(serverList.length() - 2);
            con.sendMessage(ChatColor.GOLD + "Servers: " + serverList.toString());
            con.sendMessage(ChatColor.GOLD + "Join them with: /server " + ChatColor.YELLOW + "name");
        } else {
            final String server = args[0];
            if (!servers.contains(server)) {
                con.sendMessage(ChatColor.RED + "The specified server does not exist");
            } else {
                con.connect(ProxyServer.getInstance().getServerInfo(server));
            }
        }
    }
}