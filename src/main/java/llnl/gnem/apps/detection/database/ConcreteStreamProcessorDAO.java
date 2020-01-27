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
package llnl.gnem.apps.detection.database;

import java.io.File;
import llnl.gnem.apps.detection.FrameworkPreprocessorParams;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.apps.detection.streams.ConcreteStreamProcessor;
import llnl.gnem.apps.detection.util.initialization.StreamInfo;
import llnl.gnem.apps.detection.util.initialization.StreamsConfig;
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
            try (PreparedStatement stmt = conn.prepareStatement("select stream_name, stream_dir, stream_config_file_name, PREPROCESSOR_PARAMS from stream where streamid = ? ")) {

                stmt.setInt(1, streamid);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String streamName = rs.getString(1);
                        String dir = rs.getString(2);
                        String dfile = rs.getString(3);
                        File aFile = new File(dir, dfile);
                        Map<String, StreamInfo> streams = new HashMap<>();

                        StreamInfo info = new StreamInfo(aFile.getAbsolutePath());
                        streams.put(info.getStreamName(), info);

                        StreamsConfig.getInstance().populateMap(streams);
                        Blob blob = rs.getBlob(4);
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
