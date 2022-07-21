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
package llnl.gnem.core.dataAccess;

import java.util.ArrayList;
import java.util.StringTokenizer;
import llnl.gnem.core.dataAccess.SeismogramSourceInfo.SourceType;

import llnl.gnem.core.dataAccess.database.javadb.DerbyContinuousWaveformDAO;
import llnl.gnem.core.dataAccess.database.javadb.DerbyFilterDAO;

import llnl.gnem.core.dataAccess.database.javadb.JavaDbConnectionManager;

import llnl.gnem.core.dataAccess.database.oracle.OracleContinuousWaveformDAO;
import llnl.gnem.core.dataAccess.database.oracle.OracleFilterDAO;

import llnl.gnem.core.dataAccess.interfaces.ContinuousWaveformDAO;
import llnl.gnem.core.dataAccess.interfaces.FilterDAO;

import llnl.gnem.core.database.ConnectionManager;
import llnl.gnem.core.database.Connections;
import llnl.gnem.core.database.GenericAdminValidator;
import llnl.gnem.core.database.GenericRoleManager;
import llnl.gnem.core.database.Role;
import llnl.gnem.core.database.RoleManager;
import llnl.gnem.core.database.login.LoginManager;

import llnl.gnem.core.dataAccess.streaming.FDSNContinuousWaveformDAO;

/**
 *
 * @author dodge1
 */
public class DAOFactory {

    public static final String DATA_SOURCE_NOT_SET_IN_FACTORY = "DataSource not set in factory!";
    private static final DataSource DATA_SOURCE;
    private static Connections connections;
    private static GenericRoleManager roleManager;
    private SeismogramSourceInfo seismogramSourceInfo;

    static {
        String value = System.getProperty("database_type", "ORACLE");

        if (DataSource.DERBY.name().equalsIgnoreCase(value)) {
            DATA_SOURCE = DataSource.DERBY;
        } else if (DataSource.ORACLE.name().equalsIgnoreCase(value)) {
            createRoleManager();
            DATA_SOURCE = DataSource.ORACLE;
        } else {
            throw new IllegalStateException("Unrecognized database type: " + value);
        }
    }

    private static void createRoleManager() {
        ArrayList<Role> applicationRoles = new ArrayList<>();
        String value;
        value = System.getProperty("role_string");// comma-delimited string matching one or more names from Role.java enum.
        if (value != null && !value.isEmpty()) {
            StringTokenizer st = new StringTokenizer(value, ",");

            while (st.hasMoreTokens()) {
                applicationRoles.add(Role.valueOf(st.nextToken()));
            }

        }

        roleManager = new GenericRoleManager(applicationRoles);
    }

    /**
     * Returns the DataSource (Database) that is being used at Runtime.
     *
     * @return the {
     * @See DataSource}
     */
    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    /**
     * @return true if Database Credentials for the user are required in order
     * to connect to the database.
     */
    public static boolean isRequireAuthentication() {
        return DATA_SOURCE == DataSource.ORACLE;
    }

    /**
     * @return true if the runtime database supports multiple connections from
     * multiple users.
     */
    public static boolean isMultiUserDb() {
        return DATA_SOURCE == DataSource.ORACLE;
    }

    private DAOFactory() {

        switch (DATA_SOURCE) {
            case ORACLE:
                GenericAdminValidator validator = new GenericAdminValidator();

                if (connections == null) {
                    LoginManager.login(validator, roleManager, null);
                    connections = ConnectionManager.getInstance();
                }
                break;
            case DERBY:
                connections = JavaDbConnectionManager.getInstance();
                break;
            default:
                throw new IllegalStateException(DATA_SOURCE_NOT_SET_IN_FACTORY);
        }

        String sourceTypeString = System.getProperty("SOURCE_TYPE");
        if (sourceTypeString == null) {
            seismogramSourceInfo = new SeismogramSourceInfo();
        } else {
            SeismogramSourceInfo.SourceType sourceType = SeismogramSourceInfo.SourceType.valueOf(sourceTypeString);
            if (sourceType == SeismogramSourceInfo.SourceType.Type2Database) {
                seismogramSourceInfo = new SeismogramSourceInfo();
            } else {
                String sourceIdentifier = System.getProperty("SOURCE_IDENTIFIER");
                if (sourceIdentifier == null) {
                    throw new IllegalStateException("No source identifer specified for non-default source_type!");
                } else {
                    seismogramSourceInfo = new SeismogramSourceInfo(sourceType, sourceIdentifier);
                }
            }
        }

    }

    public void setSeismogramSourceInfo(SeismogramSourceInfo seismogramSourceInfo) {
        this.seismogramSourceInfo = seismogramSourceInfo;
    }

    public SeismogramSourceInfo getSeismogramSourceInfo() {
        return seismogramSourceInfo;
    }

    public static DAOFactory getInstance() {
        return DAOFactoryHolder.INSTANCE;
    }

    /**
     * This is a convenience method that will perform the connection to the
     * database. The user's credentials are passed in. This method is used by
     * the command line tools.
     *
     * @param username
     * @param password
     * @param instance
     * @param manager
     * @return DAOFactory instance
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InterruptedException
     */
    public static DAOFactory getInstance(String username, String password, String instance, RoleManager manager) throws Exception {

        connections = ConnectionManager.getInstance(username, password, instance);

        if (!ConnectionManager.getInstance().setApplicationRoles(manager)) {
            ConnectionManager.releaseInstance();
            throw new IllegalStateException("Unable to set necessary application role(s): " + manager.getAvailableRoles());
        }

        return DAOFactoryHolder.INSTANCE;
    }

    public synchronized Connections getConnections() {
        return connections;
    }

    private static class DAOFactoryHolder {

        private static final DAOFactory INSTANCE = new DAOFactory();
    }

    public synchronized ContinuousWaveformDAO getContinuousWaveformDAO() {
        SourceType type = seismogramSourceInfo.getSourceType();
        if (type == SourceType.FDSN) {
            FDSNContinuousWaveformDAO.getInstance().setAgency(seismogramSourceInfo.getAgency());
            return FDSNContinuousWaveformDAO.getInstance();
        } else {
            switch (DATA_SOURCE) {
                case ORACLE:
                    return new OracleContinuousWaveformDAO();
                case DERBY:
                    return new DerbyContinuousWaveformDAO();
                default:
                    throw new IllegalStateException(DATA_SOURCE_NOT_SET_IN_FACTORY);
            }
        }
    }

    public synchronized FilterDAO getFilterDAO() {
        switch (DATA_SOURCE) {
            case ORACLE:
                return OracleFilterDAO.getInstance();
            case DERBY:
                return new DerbyFilterDAO();
            default:
                throw new IllegalStateException(DATA_SOURCE_NOT_SET_IN_FACTORY);
        }
    }
}
