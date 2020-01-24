/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.database;

import llnl.gnem.apps.detection.FrameworkPreprocessorParams;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.core.database.ConnectionManager;

/**
 *
 * @author dodge1
 */
public class ConcreteStreamProcessorDAO {

    private ConcreteStreamProcessorDAO() {
    }

    public static ConcreteStreamProcessorDAO getInstance() {
        return ConcreteStreamProcessorDAOHolder.INSTANCE;
    }

    private static class ConcreteStreamProcessorDAOHolder {

        private static final ConcreteStreamProcessorDAO INSTANCE = new ConcreteStreamProcessorDAO();
    }

    public ConcreteStreamProcessor createStreamProcessor(int streamid, double maxTemplateLengthSeconds, boolean triggerOnlyOnCorrelators) throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement("select stream_name, PREPROCESSOR_PARAMS from stream where streamid = ? ")) {

                stmt.setInt(1, streamid);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String streamName = rs.getString(1);
                        Blob blob = rs.getBlob(2);
                        if (!rs.wasNull()) {
                            try (InputStream is = blob.getBinaryStream()) {
                                try (ObjectInputStream oip = new ObjectInputStream(is)) {
                                    Object obj = oip.readObject();
                                    FrameworkPreprocessorParams params = (FrameworkPreprocessorParams) obj;
                                    return StreamProcessorDAO.createStreamProcessor(params, streamid, streamName, maxTemplateLengthSeconds, triggerOnlyOnCorrelators, conn);
                                }
                            }
                        }
                    }
                }
                return null;
            }
        } finally {

            if (conn != null) {
                ConnectionManager.getInstance().checkIn(conn);
            }
        }
    }

}
