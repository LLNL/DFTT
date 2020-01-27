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
package llnl.gnem.core.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.core.database.login.DbCredentials;

import llnl.gnem.core.database.login.DbServiceInfo;
import llnl.gnem.core.database.login.DbServiceInfoManager;
import llnl.gnem.core.util.ApplicationLogger;
import oracle.jdbc.driver.OracleDriver;

/*
 *  COPYRIGHT NOTICE
 *  RBAP Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */
/**
 * ConnectionManager is a Singleton class that manages access to a pool of
 * connections to the Oracle database
 *
 * @author Doug Dodge
 */
@SuppressWarnings({"AssignmentToNull"})
public class ConnectionManager implements Connections {

    private Connection conn;
    private static ConnectionManager instance;
    private final String password;
    private final String connSpec;
    private final static int DEFAULT_POOL_SIZE = 2;
    private final static int MAX_POOL_SIZE = 20;
    private final ArrayBlockingQueue<Connection> available;
    private String setRoleCommand;
    private int numConnections = 0;
    private AtomicBoolean rolesAreSet = new AtomicBoolean(true);
    private final Timer connRefreshTimer;
    private static final long INITIAL_DELAY = 2*60*1000;
    private static final long REFRESH_RATE = 4*60*1000;

    /**
     * Gets the single instance of the ConnectionManager object.
     *
     * @return The single instance of the ConnectionManager object
     */
    public synchronized static ConnectionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Attempt to access unitialized connection.");
        }
        return instance;
    }

    /**
     * Gets the single instance of the ConnectionManager object after creating
     * the instance. This must be called before using the no-argument instance
     * method.
     *
     * @param login Oracle login name
     * @param password Oracle password
     * @return The instance value
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws SQLException Thrown by DriverManager and passed on.
     */
    public synchronized static ConnectionManager getInstance(String login, String password) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, SQLException {
        if (instance == null) {
            instance = new ConnectionManager(login, password);
        }
        return instance;
    }

    public synchronized static ConnectionManager getInstance(String login, String password, String server, int port, String sid) throws ClassNotFoundException, IOException, SQLException {
        if (instance == null) {
            instance = new ConnectionManager(login, password, server, port, sid);
        }
        return instance;

    }

    public synchronized static ConnectionManager getInstance(DbCredentials credentials) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, SQLException {
        return getInstance(credentials.username, credentials.password, credentials.instance);
    }

    public synchronized static ConnectionManager getInstance(String login, String password, String sid) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, SQLException  {
        if (instance == null) {
            instance = new ConnectionManager(login, password, sid);
        }
        return instance;
    }

    public synchronized static void releaseInstance() {
        if (instance == null) {
        } else {
            try {
                instance.closeAllConnections();
                instance = null;
            } catch (Exception e) {
                ApplicationLogger.getInstance().log(Level.SEVERE, "failed releasing instance!", e);
            }
        }
    }

    private void closeAllConnections() {
        try {
            if (connRefreshTimer != null) {
                connRefreshTimer.cancel();
            }
            conn.close();
            conn = null;
            for (int j = 0; j < numConnections; ++j) {
                Connection aConn = available.take();
                aConn.close();
            }

        } catch (SQLException | InterruptedException e) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Failed closing all connections!", e);
        }

    }

    public synchronized static boolean hasInstance() {
        return instance != null;
    }

    /**
     * Gets the connection to the database
     *
     * @return The connection
     */
    public synchronized Connection getConnection() {
        return conn;
    }

    private ConnectionManager(String loginName, String password) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {

        // Initialize the Oracle driver and establish a connection. This assumes that
        // you have already acquired a user name and password.
        DbServiceInfoManager dsim = DbServiceInfoManager.getInstance();
        DbServiceInfo dsi = dsim.getSelectedService();
        conn = dsi.initializeConnection(loginName, password);
        connSpec = dsi.getConnSpec();
        conn.setAutoCommit(false);
        ConnectedUser.createInstance(loginName);
        this.password = password;
        available = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
        initializePool();
        connRefreshTimer = new Timer(true);
        connRefreshTimer.schedule(new RefreshTask(),  INITIAL_DELAY, REFRESH_RATE);
    }

    private ConnectionManager(int derbyPort, String loginName, String password) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        connSpec = String.format("jdbc:derby://localhost:%d/detector;user=%s;password=%s", derbyPort, loginName, password);
        Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
        System.setProperty("database_type", "JavaDB");
        conn = DriverManager.getConnection(connSpec);
        conn.setAutoCommit(false);
        ConnectedUser.createInstance(loginName);
        this.password = password;
        //     connections = new CopyOnWriteArrayList<Connection>();
        available = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
        initializePool();
        connRefreshTimer = null;
    }

    public static Connection createConnection(DbServiceInfo serviceInfo, String login, String password) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        Connection aConn = serviceInfo.initializeConnection(login, password);
        aConn.setAutoCommit(false);
        return aConn;
    }

    public static Connection createConnection(DbCredentials credentials) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, SQLException {
        return createConnection(credentials.getUsername(), credentials.getPassword(), credentials.getInstance());
    }

    public static Connection createConnection(String login, String password, String sid) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, SQLException  {
        DbServiceInfoManager dsim = DbServiceInfoManager.getInstance();
        if (sid != null && !sid.isEmpty()) {
            dsim.setServiceBySid(sid);
        }
        DbServiceInfo dsi = dsim.getSelectedService();
        Connection conn = dsi.initializeConnection(login, password);
        conn.setAutoCommit(false);
        return conn;

    }

    private ConnectionManager(String loginName, String password, String sid) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, SQLException  {
        DbServiceInfoManager dsim = DbServiceInfoManager.getInstance();
        if (sid != null && !sid.isEmpty()) {
            dsim.setServiceBySid(sid.toLowerCase());
        }
        DbServiceInfo dsi = dsim.getSelectedService();
        conn = dsi.initializeConnection(loginName, password);
        conn.setAutoCommit(false);
        connSpec = dsi.getConnSpec();
        ConnectedUser.createInstance(loginName);
        this.password = password;
        available = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
        initializePool();
        connRefreshTimer = new Timer(true);
        connRefreshTimer.schedule(new RefreshTask(),  INITIAL_DELAY, REFRESH_RATE);

    }

    private ConnectionManager(String loginName, String password, String server, int port, String sid) throws SQLException, IOException, ClassNotFoundException {

        // Initialize the Oracle driver and establish a connection. This assumes that
        // you have already acquired a user name and password.
        DriverManager.registerDriver(new OracleDriver());
        StringBuilder sb = new StringBuilder("jdbc:oracle:thin" + ":@" + server);
        sb.append(':');
        sb.append(port);
        sb.append('/');
        sb.append(sid);
        connSpec = sb.toString();
        conn = getConnectionWithRetry(connSpec, loginName, password);
        conn.setAutoCommit(false);
        ConnectedUser.createInstance(loginName);
        this.password = password;
        available = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
        initializePool();
        connRefreshTimer = new Timer(true);
        connRefreshTimer.schedule(new RefreshTask(),  INITIAL_DELAY, REFRESH_RATE);
    }

    private synchronized void initializePool() throws SQLException {
        for (int j = 0; j < DEFAULT_POOL_SIZE; ++j) {
            Connection connection = getConnectionWithRetry(connSpec, ConnectedUser.getInstance().getUser(), password);
            connection.setAutoCommit(false);
            try {
                available.put(connection);
                ++numConnections;
            } catch (InterruptedException ex) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Interrupted while initializing pool!", ex);
            }
        }
    }

    @Override
    public Connection checkOut() throws SQLException {
        while (!rolesAreSet.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ApplicationLogger.getInstance().log(Level.WARNING, "Interrupted while waiting for roles to be set!", ex);
            }
        }
        ApplicationLogger.getInstance().log(Level.FINEST, String.format("Before checkout %d out of %d total are available.", available.size(), numConnections));
        Connection aConn = available.poll();
        if (aConn != null) {
            ApplicationLogger.getInstance().log(Level.FINEST, String.format("Checking out %s from queue...", aConn));
            return aConn;
        } else {
            synchronized (this) {
                // if you don't synchronize here, you can create more connections than the MAX_POOL_SIZE was intended to allow
                // this will cause you troubles later as the ArrayBlockingList only has one lock for both put and pull
                if (numConnections < MAX_POOL_SIZE) {
                    aConn = getNewConnection();
                    ApplicationLogger.getInstance().log(Level.FINEST, String.format("Checking out new connection %s..", aConn));
                    return aConn;
                } else {
                    try {
                        return available.take();
                    } catch (InterruptedException ex) {
                        ApplicationLogger.getInstance().log(Level.FINE, "Operation was cancelled.");
                        return null;
                    }
                }
            }
        }
    }

    private synchronized Connection getNewConnection() throws SQLException {
        Connection connection = getConnectionWithRetry(connSpec, ConnectedUser.getInstance().getUser(), password);
        ApplicationLogger.getInstance().log(Level.FINEST, "Created connection: " + connection);
        connection.setAutoCommit(false);
        if (setRoleCommand != null) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(setRoleCommand);
            }
        }
        ++numConnections;
        return connection;
    }


    @Override
    public void checkIn(Connection connection) {
        if (connection != null && connection != conn) {

            try {
                available.put(connection);
                connection.commit();
                ApplicationLogger.getInstance().log(Level.FINEST, String.format("After check-in of %s: %d are available out of %d total", connection, available.size(), numConnections));
            } catch (InterruptedException ex) {
                ApplicationLogger.getInstance().log(Level.FINE, "Operation was cancelled.");
            } catch (SQLException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Set the roles for an applications interaction with the database.
     *
     * @param roleManager
     * @return
     * @throws SQLException
     * @throws java.lang.InterruptedException
     */
    public synchronized boolean setApplicationRoles(RoleManager roleManager) throws SQLException, InterruptedException {
        rolesAreSet.set(false);
        roleManager.setRoles(conn);
        if (!roleManager.hasRoles()) {
            rolesAreSet.set(true);
            return true; // No role was available.
        }

        Collection<Role> roles = roleManager.getAvailableRoles();
        setRoleForPool(roles);
        return roleManager.isRunnable();
    }

    private void setRoleForPool(Collection<Role> roles) throws SQLException, InterruptedException {
        createSetRoleCommand(roles);

        if (setRoleCommand != null) {
            Statement stmt = conn.createStatement();
            stmt.execute(setRoleCommand);
            stmt.close();

            Iterator<Connection> it = available.iterator();
            while (it.hasNext()) {
                Connection aConn = it.next();
                stmt = aConn.createStatement();
                stmt.execute(setRoleCommand);
                ApplicationLogger.getInstance().log(Level.FINEST, String.format("%s on %s", setRoleCommand, aConn));
                stmt.close();
            }
        }
        rolesAreSet.set(true);
    }

    private void createSetRoleCommand(Collection<Role> roles) {
        StringBuilder sb = new StringBuilder("SET ROLE ");
        if (roles.isEmpty()) {
            setRoleCommand = null;
        } else {
            for (Role role : roles) {
                sb.append(role.toString());
                sb.append(", ");
            }
            setRoleCommand = sb.toString().trim();
            setRoleCommand = setRoleCommand.substring(0, setRoleCommand.lastIndexOf(',')); // remove trailing comma
        }
    }

    private Connection getConnectionWithRetry(String connSpec, String loginName, String password) throws SQLException {
        try {
            Connection aConn = DriverManager.getConnection(connSpec, loginName, password);
            return aConn;

        } catch (Exception e) {
            int idx = connSpec.lastIndexOf('.'); // try stripping domain name...
            if (idx > 0) {
                Connection aConn = DriverManager.getConnection(connSpec.substring(0, idx), loginName, password);
                return aConn;
            } else {
                throw new IllegalStateException("Failed to create connection for: " + connSpec);
            }
        }
    }

    public synchronized int getAvailableConnectionCount() {
        return available.size();
    }

    class RefreshTask extends TimerTask {
        private static final int ONE = 1;

        @Override
        public void run() {
            if (rolesAreSet.get()) {
                PreparedStatement stmt = null;
                ResultSet rs = null;
                for (int i = 0; i < available.size(); i++) {
                    try {
                        Connection aConn = available.poll(ONE, TimeUnit.MINUTES);
                        if (aConn != null) {
                            ApplicationLogger.getInstance().log(Level.FINEST, String.format("Refreshing connection %s..", aConn));
                            stmt = conn.prepareStatement("select 1 from DUAL");
                            rs = stmt.executeQuery();
                            aConn.commit();
                            available.put(aConn);
                            ApplicationLogger.getInstance().log(Level.FINEST,
                                            String.format("After refresh of %s: %d are available out of %d total", aConn,
                                                            available.size(), getAvailableConnectionCount()));
                        } else {
                            ApplicationLogger.getInstance().log(Level.FINEST, "Refreshing connection poll returned null.");
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ConnectionManager.class.getName()).log(Level.FINEST, "RefreshTask has been interrupted.",
                                        ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(ConnectionManager.class.getName()).log(Level.FINEST, "RefreshTask Threw.", ex);
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException e) {
                                Logger.getLogger(ConnectionManager.class.getName()).log(Level.FINEST, "Exception closing ResultSet.", e);
                            }
                        }

                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (SQLException e) {
                                Logger.getLogger(ConnectionManager.class.getName()).log(Level.FINEST, "Exception closing PreparedStatement.", e);
                            }
                        }
                    }
                }
            }
        }
    }
}
