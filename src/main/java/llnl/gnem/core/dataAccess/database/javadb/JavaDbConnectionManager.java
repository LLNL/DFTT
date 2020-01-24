/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.javadb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import llnl.gnem.core.database.ConnectedUser;
import llnl.gnem.core.database.Connections;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class JavaDbConnectionManager implements Connections {

    private static final String JDBC_DERBY = "jdbc:derby:detection";
    private static final int MAX_POOL_SIZE = 1;
    private final ArrayBlockingQueue<Connection> available;

    private static JavaDbConnectionManager instance;

    static {
        try {
            instance = new JavaDbConnectionManager();
        } catch (Exception ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed to initialize JavaDbConnectionManager!", ex);
        }
    }

    public synchronized static JavaDbConnectionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Attempt to access unitialized connection.");
        }
        return instance;
    }
    private final Connection conn;

    private JavaDbConnectionManager() throws Exception {
        String connSpec = String.format(JDBC_DERBY);
        // Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        conn = DriverManager.getConnection(connSpec);
        conn.setAutoCommit(false);
        ConnectedUser.createInstance("app");
        available = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
        available.put(conn);

        // add a shutdown hook, trying to keep derby db clean
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    DriverManager.getConnection(JDBC_DERBY + ";shutdown=true");
                } catch (SQLNonTransientConnectionException e) {
                    // don't care; normal for the db shutdown case
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public synchronized Connection checkOut() throws SQLException {
        return conn;
    }

    @Override
    public synchronized void checkIn(Connection conn) throws SQLException {
    }

}

