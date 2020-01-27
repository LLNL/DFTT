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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.correlation.CorrelationComponent;
import llnl.gnem.core.correlation.clustering.GroupData;
import llnl.gnem.core.database.ConnectionManager;

public class ClustersDAO {

    private ClustersDAO() {
    }

    public static ClustersDAO getInstance() {
        return ClustersDAOHolder.INSTANCE;
    }

    public void writeClusters(Collection<GroupData> groups, int detectorid, int runid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            String sql = String.format("insert into %s values (clusterid.nextval,?,?, sysdate)", "clustered_detection");
            stmt = conn.prepareStatement(sql);
            for (GroupData gd : groups) {
                stmt.setInt(1, runid);
                stmt.setInt(2, detectorid);
                stmt.execute();
                writeClusterMembers(gd, conn);
                conn.commit();
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    private void writeClusterMembers(GroupData gd, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {

            String sql = String.format("insert into %s values (clusterid.currval,?,?,?)", "cluster_member");
            stmt = conn.prepareStatement(sql);

            ArrayList<CorrelationComponent> comps = gd.getAssociatedInfo();
            for (CorrelationComponent cc : comps) {
                double correlation = cc.getCorrelation();
                double shift = cc.getShift();
                long detectionid = cc.getEvent().getEvid();
                stmt.setLong(1, detectionid);
                stmt.setDouble(2, correlation);
                stmt.setDouble(3, shift);
                stmt.execute();
            }

        } finally {
            if (stmt != null) {
                stmt.close();
            }

        }
    }

    private static class ClustersDAOHolder {

        private static final ClustersDAO INSTANCE = new ClustersDAO();
    }

}
