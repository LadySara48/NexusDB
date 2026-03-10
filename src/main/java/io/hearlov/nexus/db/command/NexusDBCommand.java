package io.hearlov.nexus.db.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import io.hearlov.nexus.db.Main;

public class NexusDBCommand extends Command {

    public NexusDBCommand(){
        super("nexusdb", "Shows about NexusDB", "/nexusdb");
        this.setPermission("nexusdb.about");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args){
        Main base = Main.getInstance();
        String message = "\n§8§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                " §6§lNEXUS DATABASE §8| §7v" + base.getDescription().getVersion() + "\n" +
                "\n" +
                " §7▪ §fAuthor: §e" + String.join(", ", base.getDescription().getAuthors()) + "\n" +
                " §7▪ §fAPI Version: §b" + base.getDescription().getCompatibleAPIs().getFirst() + "+\n" +
                " §7▪ §fActive Databases: §a" + base.getList().size() + "\n" +
                "\n" +
                " §7" + base.getDescription().getDescription() + "\n" +
                "§8§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
        sender.sendMessage(message);
        return true;
    }
}