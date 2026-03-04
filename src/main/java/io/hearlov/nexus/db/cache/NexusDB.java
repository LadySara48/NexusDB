package io.hearlov.nexus.db.cache;

import io.hearlov.nexus.db.Main;

import io.hearlov.nexus.db.scheduler.DBTask;
import io.hearlov.nexus.db.scheduler.DBThread;
import io.hearlov.nexus.db.scheduler.H2Thread;
import io.hearlov.nexus.db.scheduler.MySQLThread;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class NexusDB{

        public final String scheduleName;
        public final DBType dbType;

        public final String dbPath;

        public final BlockingQueue<DBTask> queue = new LinkedBlockingQueue<>();
        public final DBThread thread;

    /**
     * for MySQL database
     * @param scheduleName Scheduler name
     * @param host Host IP
     * @param port Host Port
     * @param database DB Name
     * @param username DB Username
     * @param password DB User password
     */
    public NexusDB(String scheduleName, String host, int port, String database, String username, String password){
        this.dbType = DBType.MySQL;
        this.scheduleName = scheduleName;
        this.dbPath = null;

        this.thread = new MySQLThread(scheduleName, queue, host, port, database, username, password);
        this.thread.start();

        Main.getInstance().register(this);
    }

    /**
     * for H2 file database
     * @param scheduleName Scheduler name
     * @param dbPath DB Path
     */
    public NexusDB(String scheduleName, String dbPath){
        this.dbType = DBType.H2;
        this.scheduleName = scheduleName;
        this.dbPath = dbPath;

        this.thread = new H2Thread(scheduleName, queue, dbPath);
        this.thread.start();

        Main.getInstance().register(this);
    }

    public void addTask(String query, Object[] params, Consumer<List<Map<String, Object>>> callback){
        queue.add(new DBTask(query, params, callback));
    }

    public void stop(){
        this.thread.interrupt();
    }

}
