package to.joe.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import to.joe.bungee.BunJ2;

public class CommandReloadAdmins extends Command {

    private final BunJ2 j2;

    public CommandReloadAdmins(BunJ2 j2) {
        super("reloadadmins", "j2.srstaff");
        this.j2 = j2;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.j2.adminReload();
        sender.sendMessage("Wakka wakka. Reloaded.");
    }
}