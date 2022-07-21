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
package llnl.gnem.apps.detection.dataAccess.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.apps.detection.dataAccess.dataobjects.Trigger;
import llnl.gnem.apps.detection.dataAccess.DetectionDAOFactory;
import llnl.gnem.apps.detection.dataAccess.interfaces.TriggerDAO;
import llnl.gnem.apps.detection.triggerProcessing.EvaluatedTrigger;
import llnl.gnem.core.dataAccess.DataAccessException;

/**
 *
 * @author dodge1
 */
public abstract class DbTriggerDAO implements TriggerDAO {

    @Override
    public abstract Trigger writeNewTrigger(EvaluatedTrigger evaluatedTrigger) throws DataAccessException;

    @Override
    public Collection<Double> getFeatureValues(int runid, String columnName) throws DataAccessException {
        try {
            return getFeatureValuesP(runid, columnName);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    @Override
    public double getAverageSignalDuration(int runid, int detectorid) throws DataAccessException {
        try {
            return getAverageSignalDurationP(runid, detectorid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    @Override
    public double getMeanDuration(int runid, int detectorid) throws DataAccessException {
        try {
            return getMeanDurationP(runid, detectorid);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private double getMeanDurationP(int runid, int detectorid) throws SQLException {
        Connection conn = null;
        String sql = String.format("select signal_duration from %s where runid = ? and detectorid = ?",
                TableNames.getTriggerRecordTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                stmt.setInt(2, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {
                    double mean = 0;
                    int count = 0;
                    while (rs.next()) {
                        double value = rs.getDouble(1);
                        if (rs.wasNull()) {
                            return 50.0;
                        } else {
                            mean += value;
                            ++count;
                        }
                    }
                    if (count == 0) {
                        return 50.0;
                    } else {
                        return mean / count;
                    }

                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }

    private Collection<Double> getFeatureValuesP(int runid, String columnName) throws SQLException {
        Collection<Double> result = new ArrayList<>();
        String sql = String.format("select %s from %s a, "
                + "%s b where runid = ? and a.triggerid = b.triggerid",
                columnName,
                TableNames.getTriggerRecordTable(),
                TableNames.getTriggerDataFeatureTable());
        Connection conn = null;

        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, runid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getDouble(1));
                    }
                }
                return result;
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }

    }

    private double getAverageSignalDurationP(int runid, int detectorid) throws SQLException {
        Connection conn = null;
        String sql = String.format("select /*+ parallel(24) */ avg(signal_duration) from %s where runid = ? and detectorid = ? and rejected = 'n'",
                TableNames.getTriggerRecordTable());
        try {
            conn = DetectionDAOFactory.getInstance().getConnections().checkOut();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, runid);
                stmt.setInt(2, detectorid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        return rs.getDouble(1);
                    }
                    return -1;
                }
            }
        } finally {
            DetectionDAOFactory.getInstance().getConnections().checkIn(conn);
        }
    }
}
