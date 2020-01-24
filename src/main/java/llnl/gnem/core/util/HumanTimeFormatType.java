package llnl.gnem.core.util;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Dec 8, 2005
 */
public enum HumanTimeFormatType {
    YEAR_JDAY( "yyyyDDD" ),
    YEAR_MONTH_DAY( "yyyy/MM/dd" ),
    YEAR_JDAY_TIME( "yyyy/DDD-HH:mm:ss.SSS" ),
    LONG_FORMAT( "yyyy/MM/dd (DDD) HH:mm:ss.SSS" );

    private HumanTimeFormatType( String infoString )
    {
        this.infoString = infoString;
    }

    public String toString()
    {
        return infoString;
    }

    private String infoString;
}
