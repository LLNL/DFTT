package llnl.gnem.apps.detection.dataAccess.database.javadb;

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
public class JavaDbInMemConnectionManager implements Connections {

    private static final String DERBY_BACKUP_DIR = System.getProperty("DERBY_BACKUP_DIR");
    private static final String JDBC_DERBY_DETECTION = "jdbc:derby:memory:detection;restoreFrom=" + DERBY_BACKUP_DIR;

    private static final int MAX_POOL_SIZE = 1;
    private static JavaDbInMemConnectionManager instance;

    private final ArrayBlockingQueue<Connection> available;

    private final Connection conn;

    static {
        try {
            instance = new JavaDbInMemConnectionManager();
        } catch (Exception ex) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed to initialize JavaDbConnectionManager!", ex);
        }
    }

    public synchronized static JavaDbInMemConnectionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Attempt to access unitialized connection.");
        }
        return instance;
    }

    private JavaDbInMemConnectionManager() throws Exception {
        ApplicationLogger.getInstance().log(Level.INFO, "JDBC_DERBY_DETECTION: " + JDBC_DERBY_DETECTION);
        String connSpec = String.format(JDBC_DERBY_DETECTION);
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
                    DriverManager.getConnection(JDBC_DERBY_DETECTION + ";shutdown=true");
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
        // right now there is a pool of 1.
    }

}
