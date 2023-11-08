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
package llnl.gnem.dftt.core.util;

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



