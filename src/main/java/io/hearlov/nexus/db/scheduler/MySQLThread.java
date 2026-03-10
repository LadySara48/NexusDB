package io.hearlov.nexus.db.scheduler;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class MySQLThread extends DBThread{

    private final BlockingQueue<DBTask> queue;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String scheduleName;
    private final Queue<DBReturn> mainqueue;

    public MySQLThread(String scheduleName, BlockingQueue<DBTask> queue, Queue<DBReturn> mainqueue, String host, int port, String database, String username, String password){
        this.queue = queue;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.scheduleName = scheduleName;
        this.mainqueue = mainqueue;
    }

    @Override
    public void run(){
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(host);
        ds.setPort(port);
        ds.setDatabaseName(database);
        ds.setUser(username);
        ds.setPassword(password);

        //MySQL Connection Properties
        try {
            ds.setUseSSL(false);
            ds.setAllowPublicKeyRetrieval(true);
            ds.setAutoReconnect(true);
            ds.setCharacterEncoding("UTF-8");
        } catch (SQLException e) {
            System.err.println("MySQL Database Start error: " + e.getMessage());
        }

        System.out.println(scheduleName + ": MySQL Database starting...");
        try (Connection conn = ds.getConnection()) {
            System.out.println(scheduleName + ": MySQL Database Thread started!");

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DBTask task = queue.take(); // Sleep and wait

                    List<Map<String, Object>> results = execute(conn, task);

                    if (task.callback() != null) {
                        task.callback().accept(results);
                    }

                    if (task.maincallback() != null) {
                        this.mainqueue.offer(new DBReturn(task.maincallback(), results));
                    }

                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    System.out.println(scheduleName + ": Database thread is terminating...");
                    break;
                }catch (SQLException e){
                    System.err.println("SQL Error: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("A connection to the MySQL database could not be established: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> execute(Connection conn, DBTask task) throws SQLException{
        List<Map<String, Object>> resultList = new ArrayList<>();

        try(PreparedStatement stmt = conn.prepareStatement(task.query())){
            // Add parameters safely to the SQL query
            if (task.params() != null) {
                for (int i = 0; i < task.params().length; i++) {
                    stmt.setObject(i + 1, task.params()[i]);
                }
            }

            // If the query is SELECT (it returns a ResultSet)
            String queryUpper = task.query().trim().toUpperCase();
            if (queryUpper.startsWith("SELECT") || queryUpper.startsWith("SHOW")){
                try (ResultSet rs = stmt.executeQuery()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                        resultList.add(row);
                    }
                }
            } else {
                // Operations such as UPDATE, INSERT, DELETE, CREATE, ALTER
                int affectedRows = stmt.executeUpdate();
                resultList.add(Map.of("affected_rows", affectedRows));
            }
        }

        return resultList;
    }
}