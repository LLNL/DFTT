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
package llnl.gnem.core.dataAccess.database.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.database.TableNames;
import llnl.gnem.core.dataAccess.interfaces.EtypeDAO;
import llnl.gnem.core.metadata.EtypeInfo;

/**
 *
 * @author dodge1
 */
public class OracleEtypeDAO implements EtypeDAO {

    @Override
    public Collection<EtypeInfo> getEtypeInfo() throws DataAccessException {
        try {
            return getEtypeInfoP();
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    public Collection<EtypeInfo> getEtypeInfoP() throws SQLException {
        Collection<EtypeInfo> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;

        ResultSet rs = null;
        try {
            conn = DAOFactory.getInstance().getConnections().checkOut();
            String sql = String.format("select etype, descrip from %s", TableNames.ETYPE_DESC_TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setFetchSize(100);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String etype = rs.getString(1);
                String descrip = rs.getString(2);
                results.add(new EtypeInfo(etype, descrip));

            }
            return results;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                DAOFactory.getInstance().getConnections().checkIn(conn);
            }
        }
    }

}
