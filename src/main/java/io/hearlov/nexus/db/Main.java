package io.hearlov.nexus.db;

import cn.nukkit.plugin.PluginBase;
import io.hearlov.nexus.db.cache.NexusDB;
import io.hearlov.nexus.db.command.NexusDBCommand;
import io.hearlov.nexus.db.scheduler.DBThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main extends PluginBase{

    private final List<NexusDB> list = new ArrayList<>();
    public static final List<String> workflow = new CopyOnWriteArrayList<>();
    public static final List<String> errorflow = new CopyOnWriteArrayList<>();

    private static Main instance;
    @Override
    public void onLoad(){instance = this;}
    public static Main getInstance(){ return instance; }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getServer().getCommandMap().register("hearlovnexusdb", new NexusDBCommand());
        getServer().getScheduler().scheduleDelayedTask(this, () -> {
            if(errorflow.isEmpty()) return;
            String sync = String.join(", ", errorflow);
            System.out.println(sync + "These database tools could not be started.!");
        }, 20 * 5);
    }

    public void register(NexusDB db){
        list.add(db);
    }

    public List<NexusDB> getList(){
        return list;
    }

    public NexusDB createSQL(String scheduleName, String host, int port, String database, String username, String password){
        return new NexusDB(scheduleName, host, port, database, username, password);
    }

    public NexusDB createH2(String scheduleName, String dbPath){
        return new NexusDB(scheduleName, dbPath);
    }

}