package io.hearlov.nexus.db.cache;

import io.hearlov.nexus.db.Main;

import io.hearlov.nexus.db.scheduler.*;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class NexusDB{

        public final String scheduleName;
        public final DBType dbType;

        public final String dbPath;

        public final BlockingQueue<DBTask> queue = new LinkedBlockingQueue<>();
        public final Queue<DBReturn> mainqueue = new ConcurrentLinkedQueue<>();
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

        this.thread = new MySQLThread(scheduleName, queue, mainqueue, host, port, database, username, password);
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

        this.thread = new H2Thread(scheduleName, queue, mainqueue, dbPath, Main.getInstance().defaultserver);
        this.thread.start();

        Main.getInstance().register(this);
    }

    /**
     * Creates a NexusDB instance using an H2 file database.
     *
     * @param scheduleName The name of the scheduler (and the database thread).
     * @param dbPath       The file path for the H2 database.
     * @param multiaccess  If {@code true}, starts H2 in server mode — allowing multiple threads
     *                     and external applications to access the same database simultaneously.
     *                     <br><br>
     *                     ⚠️ <b>Note:</b> H2 databases started in server mode may have a slightly
     *                     longer initial connection time compared to standard embedded mode.
     */
    public NexusDB(String scheduleName, String dbPath, boolean multiaccess) {
        this.dbType = DBType.H2;
        this.scheduleName = scheduleName;
        this.dbPath = dbPath;

        this.thread = new H2Thread(scheduleName, queue, mainqueue, dbPath, multiaccess);
        this.thread.start();

        Main.getInstance().register(this);
    }

    /**
     * Adds a database task to the execution queue.
     * The query will be executed asynchronously on the database thread.
     * The result will be delivered via the given callback, also on the database thread.
     *
     * @param query    The SQL query to execute.
     * @param params   The parameters to bind to the query. Use {@code null} if there are no parameters.
     * @param callback Called on the <b>database thread</b> when the query completes.
     *                 Receives the result as a list of rows, where each row is a column-name-to-value map.
     */
    public void addTask(String query, Object[] params, Consumer<List<Map<String, Object>>> callback){
        queue.add(new DBTask(query, params, callback, null));
    }

    /**
     * Adds a database task to the execution queue with an additional main thread callback.
     * The query will be executed asynchronously on the database thread.
     * The result will be delivered to both callbacks — first on the database thread,
     * then forwarded to the main thread callback for safe interaction with server-side APIs.
     *
     * @param query        The SQL query to execute.
     * @param params       The parameters to bind to the query. Use {@code null} if there are no parameters.
     * @param callback     Called on the <b>database thread</b> when the query completes.
     *                     Receives the result as a list of rows, where each row is a column-name-to-value map.
     * @param maincallback Called on the <b>main thread</b> after the database callback.
     *                     Use this when you need to interact with the server API (e.g. modifying players, worlds).
     */
    public void addTask(String query, Object[] params, Consumer<List<Map<String, Object>>> callback, Consumer<List<Map<String, Object>>> maincallback){
        queue.add(new DBTask(query, params, callback, maincallback));
    }

    public void stop(){
        this.thread.interrupt();
    }

}
