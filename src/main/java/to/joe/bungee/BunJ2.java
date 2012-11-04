package to.joe.bungee;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.Permission;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.plugin.JavaPlugin;
import net.md_5.bungee.plugin.LoginEvent;
import net.md_5.bungee.plugin.ServerConnectEvent;

import to.joe.bungee.commands.CommandBanIP;
import to.joe.bungee.commands.CommandIP;
import to.joe.bungee.commands.CommandReloadAdmins;
import to.joe.bungee.commands.CommandUnbanIP;

public class BunJ2 extends JavaPlugin {

    private final Conf conf = new Conf(); // My own adaptation of a yaml config. VERY simple.
    private final Timer fiveMins = new Timer(); // I schedule a task that runs every 5 minutes!

    public void adminReload() {
        final Map<Permission, List<String>> map = SQLHandler.loadAdmins();
        if (map.containsKey(Permission.ADMIN)) {
            BungeeCord.instance.config.admins = map.get(Permission.ADMIN);
        }
        if (map.containsKey(Permission.MODERATOR)) {
            BungeeCord.instance.config.moderators = map.get(Permission.MODERATOR);
        }
    }

    @Override
    public void onDisable() {
        this.fiveMins.cancel();
        BungeeCord.instance.commandMap.remove("banip");
        BungeeCord.instance.commandMap.put("ip", new net.md_5.bungee.command.CommandIP());
        BungeeCord.instance.commandMap.remove("unbanip");
        BungeeCord.instance.commandMap.remove("reloadadmins");
    }

    @Override
    public void onEnable() {
        this.conf.load();
        BungeeCord.instance.commandMap.put("banip", new CommandBanIP());
        BungeeCord.instance.commandMap.put("ip", new CommandIP());
        BungeeCord.instance.commandMap.put("unbanip", new CommandUnbanIP());
        BungeeCord.instance.commandMap.put("reloadadmins", new CommandReloadAdmins(this));
        try {
            SQLHandler.start(this.conf.host, this.conf.port, this.conf.user, this.conf.pass, this.conf.db);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        this.fiveMins.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                BunJ2.this.cycle();
            }

        }, 1, 60000);
    }

    @Override
    public void onHandshake(LoginEvent event) { // Before mc.net auth. Checking IP only. No need to check username yet, waste of a query.
        this.check(event, false);
    }

    @Override
    public void onLogin(LoginEvent event) {
        this.check(event, true);
        final String username = event.getUsername();
        final String ip = event.getAddress().getHostAddress();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    SQLHandler.track(username, ip);
                } catch (final SQLException e) {
                }
            }
        }, 1);
    }

    @Override
    public void onServerConnect(ServerConnectEvent event) {
        if (this.conf.adminonlyservers.contains(event.getServer()) && (BungeeCord.instance.config.getPermission(event.getConnection()) == Permission.DEFAULT)) {
            event.setNewServer(null); // Setting to null means no redirect, unless new connection. In that case, default server.
        }
    }

    private void check(LoginEvent event, boolean isUsername) {
        boolean fail = false;
        try {
            if (SQLHandler.isAllowed(isUsername ? event.getUsername() : event.getAddress().getHostAddress(), isUsername)) {
                return;
            }
        } catch (final Exception e) {
            fail = true;
        }
        event.setCancelled(true);
        event.setCancelReason(fail ? this.conf.disconnectsqlfail : isUsername ? this.conf.disconnectbanned : this.conf.disconnectipbanned);
    }

    private void cycle() {
        this.adminReload();
        for (final UserConnection con : BungeeCord.instance.connections.values()) {
            try {
                if (!SQLHandler.isAllowed(con.getName(), true)) {
                    con.disconnect(this.conf.disconnectbanned);
                    continue;
                }
                final SocketAddress address = con.getAddress();
                if (address instanceof InetSocketAddress) {
                    if (!SQLHandler.isAllowed(((InetSocketAddress) address).getAddress().getHostAddress(), false)) {
                        con.disconnect(this.conf.disconnectipbanned);
                        continue;
                    }
                }
            } catch (final Exception e) {
                // Not concerned.
            }
        }
    }
}
