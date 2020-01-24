package llnl.gnem.core.gui.plotting;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

public enum HorizAlignment { LEFT ("left"), CENTER("center"), RIGHT("right)");
    private final String name;

    private HorizAlignment( String name )
    {
        this.name = name;
    }

    /**
     * Return a String description of this type.
     *
     * @return The String description
     */
@Override
    public String toString()
    {
        return name;
    }

   

    public static HorizAlignment getHorizAlignment( String str )
    {
        return HorizAlignment.valueOf(str);
    }
}

