package io.hearlov.nexus.db.scheduler;

import io.hearlov.nexus.db.Main;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class H2Thread extends DBThread{

    private final BlockingQueue<DBTask> queue;
    private final String dbPath;
    private final String scheduleName;

    public H2Thread(String scheduleName, BlockingQueue<DBTask> queue, String dbPath){
        this.queue = queue;
        this.dbPath = "jdbc:h2:" + dbPath;
        this.scheduleName = scheduleName;
    }

    @Override
    public void run(){
        // H2 için bağlantı string'i - AUTO_SERVER çoklu erişim için
        String url = dbPath /*+ ";AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1"*/;

        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(url);
        ds.setUser("sa");
        ds.setPassword("");

        try (Connection conn = ds.getConnection()) {
            Main.workflow.add(scheduleName);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DBTask task = queue.take(); // Sleep and wait

                    List<Map<String, Object>> results = execute(conn, task);

                    if (task.callback() != null) {
                        task.callback().accept(results);
                    }

                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    System.out.println(scheduleName + ": Database thread sonlandırılıyor...");
                    break;
                }catch (SQLException e){
                    System.err.println("SQL Hatası: " + e.getMessage());
                }
            }

        }catch(SQLException e){
            Main.errorflow.add(scheduleName);
        }
    }

    private List<Map<String, Object>> execute(Connection conn, DBTask task) throws SQLException{
        List<Map<String, Object>> resultList = new ArrayList<>();

        try(PreparedStatement stmt = conn.prepareStatement(task.query())){
            // Parametreleri SQL sorgusuna güvenli bir şekilde ekle
            if (task.params() != null) {
                for (int i = 0; i < task.params().length; i++) {
                    stmt.setObject(i + 1, task.params()[i]);
                }
            }

            // Eğer sorgu SELECT ise (ResultSet döner)
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
                // UPDATE, INSERT, DELETE, CREATE, ALTER gibi işlemler
                int affectedRows = stmt.executeUpdate();
                resultList.add(Map.of("affected_rows", affectedRows));
            }
        }

        return resultList;
    }
}