package llnl.gnem.core.database;

import llnl.gnem.core.deprecated.DatabaseException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by: dodge1
 * Date: Jul 9, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public interface AdminValidator {
    public boolean isAdminOptionAvailable();

    public boolean isUserAnAdmin( final String user, Connection conn ) throws DatabaseException, SQLException;

    public boolean isLoggedInUserAnAdmin();
    public void setLoggedInUserAdminStatus( boolean status );
}
