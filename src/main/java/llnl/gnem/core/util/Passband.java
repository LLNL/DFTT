package llnl.gnem.core.util;

/*
 *  COPYRIGHT NOTICE
 *  RBAP Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */


public enum Passband { BAND_PASS("BP"), BAND_REJECT("BR"), LOW_PASS("LP"), HIGH_PASS("HP");
    private final String name;

    private Passband( String name )
    {
        this.name = name;
    }

    /**
     * A string representation of the passband for a filter suitable for use with the
     * dbh.ButterworthFilter..
     *
     * @return A String with the one of the values "LP", or "HP", "BP", "BR"
     */
@Override
    public String toString()
    {
        return name;
    }

    /**
     * Get all the passband codes known to this class
     *
     * @return A String array containing all the passband codes
     */
    public static String[] getPassBandCodes()
    {
        String[] codes = {"BP", "BR", "LP", "HP"};
        return codes;
    }

    /**
     * Returns a Passband object given a 2-character String descriptor. Only recognized codes are
     * "BP", "LP", "HP", "BR". Any other String will result in a null Passband object.
     *
     * @param code The code of the desired passband object.
     * @return The specified Passband object.
     */
    public static Passband getPassbandFromString( final String code )
    {
        for(Passband pb : Passband.values()){
            if(pb.toString().equals(code))
                return pb;
        }
        throw new IllegalStateException("No Passband for code ("+code + ")!");
    }

    public static Passband[] getAvailablePassBands()
    {
        return Passband.values();
    }

}



