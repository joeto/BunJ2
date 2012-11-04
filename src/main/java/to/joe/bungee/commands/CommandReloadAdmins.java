package to.joe.bungee.commands;

import net.md_5.bungee.Permission;
import net.md_5.bungee.command.Command;
import net.md_5.bungee.command.CommandSender;

import to.joe.bungee.BunJ2;

public class CommandReloadAdmins extends Command {

    private final BunJ2 j2;

    public CommandReloadAdmins(BunJ2 j2) {
        this.j2 = j2;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (this.getPermission(sender) != Permission.ADMIN) {
            return;
        }
        this.j2.adminReload();
        sender.sendMessage("Wakka wakka. Reloaded.");
    }
}
