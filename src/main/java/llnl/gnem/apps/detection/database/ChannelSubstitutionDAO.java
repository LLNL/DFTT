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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import llnl.gnem.apps.detection.core.dataObjects.ChannelSubstitution;
import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.util.FileInputArrayLoader;

public class ChannelSubstitutionDAO {

    private ChannelSubstitutionDAO() {
    }

    public static ChannelSubstitutionDAO getInstance() {
        return ChannelSubstitutionDAOHolder.INSTANCE;
    }

    private static class ChannelSubstitutionDAOHolder {

        private static final ChannelSubstitutionDAO INSTANCE = new ChannelSubstitutionDAO();
    }

    public Map<String, ChannelSubstitution> getChannelSubstitutions(int configid) throws SQLException {
        Map<String, ChannelSubstitution> result = new HashMap<>();

        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().checkOut();
            stmt = conn.prepareStatement(String.format("select chan, chan_sub from %s where configid = ?",
                    TableNames.getInstance().getChanSubTableName()));

            stmt.setInt(1, configid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String chan = rs.getString(1);
                String sub = rs.getString(2);
                ChannelSubstitution cs = result.get(chan);
                if (cs == null) {
                    cs = new ChannelSubstitution(chan, sub);
                    result.put(chan, cs);
                } else {
                    cs.add(sub);
                }
            }
            return result;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            ConnectionManager.getInstance().checkIn(conn);
        }

    }

    public Map<String, ChannelSubstitution> getChannelSubstitutions(String substitutionFile) throws IOException {
        Map<String, ChannelSubstitution> result = new HashMap<>();
        String[] lines = FileInputArrayLoader.fillStrings(substitutionFile);

        for (String line : lines) {
            StringTokenizer st = new StringTokenizer(line);
            if (st.countTokens() == 2) {
                String chan = st.nextToken();
                String sub = st.nextToken();
                ChannelSubstitution cs = result.get(chan);
                if (cs == null) {
                    cs = new ChannelSubstitution(chan, sub);
                    result.put(chan, cs);
                } else {
                    cs.add(sub);
                }
            }
        }
        return result;

    }

    public void writeChannelSubstitutions(Map<String, ChannelSubstitution> subMap, int configid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = String.format("insert into %s values (?,?,?)", TableNames.getInstance().getChanSubTableName());
        try {
            conn = ConnectionManager.getInstance().checkOut();
            deleteSubstitutions(configid, conn);
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, configid);
            for( String chan : subMap.keySet()){
                stmt.setString(2,chan);
                ChannelSubstitution cs  = subMap.get(chan);
                Collection<String> subs = cs.getSubstitutions();
                for(String sub : subs){
                    stmt.setString(3, sub);
                    stmt.execute();
                }
            }
            conn.commit();
        } finally {
            if( stmt != null){
                stmt.close();
            }
            ConnectionManager.getInstance().checkIn(conn);
        }
    }

    private void deleteSubstitutions(int configid, Connection conn) throws SQLException {
        String sql = String.format("delete from %s where configid = ?", TableNames.getInstance().getChanSubTableName());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, configid);
            stmt.execute();
            conn.commit();
        }
    }

}
