package llnl.gnem.core.database.column;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * An exception that is thrown when an attempt is made to create a ColumnSet or
 * table with one or more columns duplicated.
 *
 * @author Doug Dodge
 */
public class DuplicateColumnException extends Exception {
    /**
     * Constructor that takes a String message.
     *
     * @param msg The message to be passed with this exception.
     */
    public DuplicateColumnException( String msg )
    {
        super( msg );
    }

    /**
     * Default Constructor for the DuplicateColumnException object
     */
    public DuplicateColumnException()
    {
        super( "" );
    }
}

