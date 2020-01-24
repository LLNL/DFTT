/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.util;

import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.deprecated.DatabaseException;

/**
 *
 * @author dodge1
 */
public class JavaPrefObjectManager {

    private static final String PREFERENCE_TABLE = "LLNL.JAVA_PREF_OBJECTS";
    private static final String READ_OBJECT_SQL = "SELECT object_value FROM llnl.java_pref_objects WHERE app_name = ? and user_name = lower(user) and object_name = ?";
    private static String appName;

    private static class JavaPrefObjectManagerHolder {
        private static final JavaPrefObjectManager instance = new JavaPrefObjectManager();
    }

    private JavaPrefObjectManager() {
    }

    public static JavaPrefObjectManager getInstance() {
        return JavaPrefObjectManagerHolder.instance;
    }

    public void setAppName(String name) {
        appName = name;
    }

    private boolean objectExists(String objectName, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("Select rowid from %s where app_name = ? and user_name = lower(user) and object_name = ?",
                    PREFERENCE_TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, appName);
            stmt.setString(2, objectName);
            rs = stmt.executeQuery();
            return rs.next();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private void writeObject(Object object) throws DatabaseException, IOException, SQLException {
        Connection conn = null;
        String className = object.getClass().getName();

        ResultSet rs = null;
        PreparedStatement stmt = null;
        OutputStream os = null;
        ObjectOutputStream oop = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            conn.setAutoCommit(false);

            if (!objectExists(className, conn)) {
                insertEmptyBlob(className, conn);
            }
            String sql = String.format("select object_value from %s  where app_name = ? and user_name = lower(user) "
                    + "and object_name = ?  for update", PREFERENCE_TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, appName);
            stmt.setString(2, className);
            rs = stmt.executeQuery();
            rs.next();
            Blob blob = (Blob) rs.getBlob(1);

            os = blob.setBinaryStream(0);
            oop = new ObjectOutputStream(os);
            oop.writeObject(object);
            oop.flush();
            conn.commit();
        } finally {
            if (oop != null) {
                oop.close();
            }
            if (os != null) {
                os.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    private Object readObject(String className, Connection conn) throws Exception {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        InputStream is = null;
        ObjectInputStream oip = null;
        try {
            stmt = conn.prepareStatement(READ_OBJECT_SQL);
            stmt.setString(1, appName);
            stmt.setString(2, className);
            rs = stmt.executeQuery();
            rs.next();
            is = rs.getBlob(1).getBinaryStream();
            oip = new AppSupportLegacyStream(is);
            return oip.readObject();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (is != null) {
                is.close();
            }
            if (oip != null) {
                oip.close();
            }
        }
    }
    
    public class AppSupportLegacyStream extends ObjectInputStream {
        public AppSupportLegacyStream() throws IOException {            
        }
        
        public AppSupportLegacyStream(InputStream in) throws IOException {
            super(in);
        }
        
        @Override
        protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
            ObjectStreamClass read = super.readClassDescriptor();
            if (read.getName().startsWith("llnl.gnem.appSupport.util.")) {
                return ObjectStreamClass.lookup(Class.forName(read.getName().replace("llnl.gnem.appSupport.util.", "llnl.gnem.core.util.")));
            }
            return read;
        }
    }

    /**
     * Retrieves a stored preferences object for the current user whose
     * fully-qualified class name matches the supplied className. If no such
     * object is found, constructs a new object for className using the default
     * constructor for className. The className string must be a fully-qualified
     * name of a class in the classpath.
     *
     * @param className
     * @return The retrieved or newly-constructed Object for className.
     * @throws DatabaseException
     */
    public Object retrieveUserPreference(String className) throws Exception {
        if (appName == null) {
            throw new IllegalStateException("App name was not set!");
        }
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();

            if (objectExists(className, conn)) {
                return readObject(className, conn);
            } else {
                return Class.forName(className).newInstance();
            }
        } catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Could not retrieve user preferences!", e);
            return Class.forName(className).newInstance();
        } finally {
            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

    /**
     * Stores a user preferences object. If an object of the same
     * fully-qualified class name exists for the current user, then the existing
     * stored preferences object is replaced with the new one. Objects to be
     * stored must implement the Serializable interface.
     *
     * @param prefObject A preferences object to be stored.
     */
    public void updateStoredPreference(Object prefObject) throws Exception {
        if (appName == null) {
            throw new IllegalStateException("App name was not set!");
        }
        writeObject(prefObject);
    }

    private void insertEmptyBlob(String className, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = String.format("insert into %s values( ?, lower(user), ?, empty_blob() )",
                    PREFERENCE_TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, appName);
            stmt.setString(2, className);
            stmt.execute();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
}
