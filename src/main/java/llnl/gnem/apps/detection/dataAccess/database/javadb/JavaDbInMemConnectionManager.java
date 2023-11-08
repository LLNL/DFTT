/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.apps.detection.dataAccess.database.javadb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

import llnl.gnem.dftt.core.database.ConnectedUser;
import llnl.gnem.dftt.core.database.Connections;
import llnl.gnem.dftt.core.util.ApplicationLogger;

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
