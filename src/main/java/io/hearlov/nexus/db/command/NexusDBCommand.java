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
        sender.sendMessage("§8§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage(" §6§lNEXUS DATABASE §8| §7v" + base.getDescription().getVersion());
        sender.sendMessage("");
        sender.sendMessage(" §7▪ §fAuthor: §e" + String.join(", ", base.getDescription().getAuthors()));
        sender.sendMessage(" §7▪ §fAPI Version: §b" + base.getDescription().getCompatibleAPIs().getFirst() + "+");
        sender.sendMessage(" §7▪ §fActive Databases: §a" + base.getList().size());
        sender.sendMessage("");
        sender.sendMessage(" §7" + base.getDescription().getDescription());
        sender.sendMessage("§8§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return true;
    }
}