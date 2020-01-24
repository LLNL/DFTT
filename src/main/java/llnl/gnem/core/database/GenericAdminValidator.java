package llnl.gnem.core.database;

import llnl.gnem.core.deprecated.DatabaseException;
import java.sql.Connection;

/**
 * Created by: dodge1
 * Date: Jul 9, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */

/**
 * This class is responsible for setting the behavior of the login module with respect to
 * the availability of an admin option checkbox. Currently, it specifies no such checkbox.
 */
public class GenericAdminValidator implements AdminValidator {

    public boolean isAdminOptionAvailable()
    {
        return false;
    }

    public boolean isUserAnAdmin( final String user, Connection conn ) throws DatabaseException
    {
        return false;
    }

    public boolean isLoggedInUserAnAdmin()
    {
        return false;
    }

    public void setLoggedInUserAdminStatus( boolean status )
    {

    }


}
