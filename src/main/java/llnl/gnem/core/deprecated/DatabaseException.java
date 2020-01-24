package llnl.gnem.core.deprecated;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

public class DatabaseException extends Exception {

    /**
     * Constructs an DatabaseException with a user-supplied message.
     *
     * @param msg The message to be embedded in this exception.
     */
    public DatabaseException( String msg )
    {
        super( msg );
        errorCode = 0;
        SQLState = null;
        SQLStatement = null;
        location = null;
    }

    public String getCURRENT_ERROR()
    {
        return CURRENT_ERROR_String;
    }

    /**
     * Constructs a fully-specified DatabaseException with a user-supplied message,
     * a SQL error code, a SQLState string (from SQLException), a SQL statement string,
     * and a string describing where the exception was thrown.
     *
     * @param msg       The user-supplied message
     * @param code      The ORA Error code
     * @param state     The SQL State string provided by Oracle
     * @param statement The SQL statement that was being executed when this exception
     *                  was thrown.
     * @param loc       A String containing the class and method from which the exception
     *                  was thrown.
     */
    public DatabaseException( String msg, int code, String state, String statement, String loc )
    {
        super( msg );
        errorCode = code;
        SQLState = state;
        SQLStatement = statement;
        location = loc;
        if( code == 4068 ){
            CURRENT_ERROR_String = ERROR_4068_String;

        }
        else{
            CURRENT_ERROR_String = null;

        }

    }

    /**
     * Retrieves the Oracle ORA error code for this object.
     *
     * @return The errorCode value
     */
    public int getErrorCode()
    {
        return errorCode;
    }

    /**
     * Gets the SQLState for this object
     *
     * @return The SQLState value
     */
    public String getSQLState()
    {
        if( SQLState != null )
            return SQLState;
        else
            return null;
    }

    /**
     * Returns the text of the SQL statement that was being executed when this exception
     * was thrown. Can be null in some cases.
     *
     * @return The SQLStatement text
     */
    public String getSQLStatement()
    {
        if( SQLStatement != null )
            return SQLStatement;
        else
            return null;
    }

    /**
     * Gets a String describing the class and method from which this exception was
     * thrown.
     *
     * @return The location value
     */
    public String getLocation()
    {
        if( location != null )
            return location;
        else
            return null;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer( getMessage() );
        sb.append( '\n' );
        sb.append( "SQL state = " );
        sb.append( getSQLState() );
        sb.append( '\n' );
        sb.append( "SQL statement = " );
        sb.append( getSQLStatement() );
        sb.append( '\n' );
        sb.append( "Location of error = " );
        sb.append( getLocation() );
        return sb.toString();
    }

    private int errorCode;
    private String SQLState;
    private String SQLStatement;
    private String location;


    String ERROR_4068_String =
            "A PL/SQL package has been recompiled while you were using this program. Please " +
                    "retry the operation. If this still does not succeed, restart the program. " +
                    "If the error persists, contact the development team.";

    private String CURRENT_ERROR_String = "";

}

