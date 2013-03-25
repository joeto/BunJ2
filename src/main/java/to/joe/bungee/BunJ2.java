package to.joe.bungee;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import com.google.common.eventbus.Subscribe;

import to.joe.bungee.commands.CommandAlert;
import to.joe.bungee.commands.CommandBanIP;
import to.joe.bungee.commands.CommandIP;
import to.joe.bungee.commands.CommandList;
import to.joe.bungee.commands.CommandReloadAdmins;
import to.joe.bungee.commands.CommandServer;
import to.joe.bungee.commands.CommandUnbanIP;

public class BunJ2 extends Plugin implements Listener {

    private final Conf conf = new Conf(); // My own adaptation of a yaml config. VERY simple.
    private final Timer fiveMins = new Timer(); // I schedule a task that runs every 5 minutes!

    private Set<String> admins;
    private Set<String> srstaff;
    
    private final String NOTICE = "[" + ChatColor.BLUE + "NOTICE" + ChatColor.RESET + "] ";
    private final Map<ServerInfo, String> maintenanceMessages = new ConcurrentHashMap<ServerInfo, String>();

    public void adminReload() {
        final Map<Admin, Set<String>> map = SQLHandler.loadAdmins();
        this.admins = map.get(Admin.ADMIN);
        this.srstaff = map.get(Admin.SRSTAFF);
        for (final ProxiedPlayer con : ProxyServer.getInstance().getPlayers()) {
            con.removeGroups("admin", "srstaff", "default");
            final String name = con.getName().toLowerCase();
            if (this.srstaff.contains(name)) {
                con.addGroups("srstaff");
            } else if (this.admins.contains(name)) {
                con.addGroups("admin");
            } else {
                con.addGroups("default");
            }
        }
    }

    public Conf getConf() {
        return this.conf;
    }

    @Override
    public void onDisable() {
        this.fiveMins.cancel();
    }

    @Override
    public void onEnable() {
        getProxy().registerChannel("SeniorFun");
        this.conf.load();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandBanIP());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandIP());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandUnbanIP());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandReloadAdmins(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandList());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandServer());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandAlert());
        ProxyServer.getInstance().getPluginManager().registerListener(this, this);
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

    @Subscribe
    public void onLogin(LoginEvent event) {
        this.check(event);
        final String username = event.getConnection().getName();
        final String ip = event.getConnection().getAddress().getAddress().getHostAddress();
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

    @Subscribe
    public void onJoin(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        if (this.srstaff.contains(player.getName().toLowerCase())) {
            player.addGroups("srstaff");
        } else if (this.admins.contains(player.getName().toLowerCase())) {
            player.addGroups("admins");
        } else {
            player.addGroups("default");
        }
    }
    
    @Subscribe
    public void onMessage(PluginMessageEvent event) {
        getProxy().getLogger().info("Received a message!");
        getProxy().getLogger().info(event.getTag());
        if (event.getTag().equals("SeniorFun")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                if (in.readUTF().equals("Maintenance")) {
                    maintenanceMessages.put(((ProxiedPlayer) event.getSender()).getServer().getInfo(), in.readUTF());
                }
            } catch (IOException e) {
                //Nope
            }
        }
    }

    @Subscribe
    public void onKick(ServerKickEvent event) {
        getProxy().getLogger().info("Kicked!");
        getProxy().getLogger().info(event.getKickReason());
        ServerInfo currentServer = event.getPlayer().getServer().getInfo();
        if (currentServer != event.getCancelServer()) {
            StringBuilder sb = new StringBuilder(NOTICE);
            boolean catchKick = false;
            switch (event.getKickReason()) {
                case "We'll be back after these brief messages":
                    sb.append(currentServer.getName() + " has shutdown.");
                    catchKick = true;
                    break;
                case "Server entering maintenance mode":
                    sb.append(currentServer.getName() + " has entered maintenance mode.");
                    catchKick = true;
                    break;
            }
            if (!catchKick && maintenanceMessages.containsValue(event.getKickReason())) {
                sb.append(currentServer.getName() + " is in maintenance mode.");
                catchKick = true;
            }
            if (catchKick) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(sb.toString());
            }
        }
    }

    private void check(LoginEvent event) {
        boolean fail = false;
        boolean username = true;
        boolean banned = false;
        try {
            if (!SQLHandler.isAllowed(event.getConnection().getName(), true)) {
                banned = true;
            }
            if (!SQLHandler.isAllowed(event.getConnection().getAddress().getAddress().getHostAddress(), false)) {
                banned = true;
                username = false;
            }
            if (!banned) {
                return;
            }
        } catch (final Exception e) {
            fail = true;
        }
        event.setCancelled(true);
        event.setCancelReason(fail ? this.conf.disconnectsqlfail : username ? this.conf.disconnectbanned : this.conf.disconnectipbanned);
    }

    private void cycle() {
        this.adminReload();
        for (final ProxiedPlayer con : ProxyServer.getInstance().getPlayers()) {
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