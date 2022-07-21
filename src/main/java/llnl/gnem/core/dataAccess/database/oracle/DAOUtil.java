/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.database.oracle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author dodge1
 */
public class DAOUtil {

    private DAOUtil() {
    }

    public static void setStringValue(String value, final PreparedStatement stmt, int jdx) throws SQLException {
        if (value == null || value.isEmpty()) {
            stmt.setNull(jdx, Types.VARCHAR);
        } else {
            stmt.setString(jdx, value);
        }
    }

    public static void setDoubleValue(Double value, final PreparedStatement stmt, int jdx) throws SQLException {
        if (value == null || value.isInfinite() || value.isNaN() || (value != 0 && Math.abs(value) < Float.MIN_NORMAL)) {
            stmt.setNull(jdx, Types.DOUBLE);
        } else {
            stmt.setDouble(jdx, value);
        }
    }

    public static void setIntegerValue(Integer value, final PreparedStatement stmt, int jdx) throws SQLException {
        if (value == null) {
            stmt.setNull(jdx, Types.INTEGER);
        } else {
            stmt.setInt(jdx, value);
        }
    }

    public static void setLongValue(Long value, final PreparedStatement stmt, int jdx) throws SQLException {
        if (value == null) {
            stmt.setNull(jdx, Types.INTEGER);
        } else {
            stmt.setLong(jdx, value);
        }
    }

    public static Double getDoubleValue(ResultSet rs, int jdx) throws SQLException {
        Double result = rs.getDouble(jdx);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }
    
   public static Long getLongValue(ResultSet rs, int jdx) throws SQLException {
        Long result = rs.getLong(jdx);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }
    
}
