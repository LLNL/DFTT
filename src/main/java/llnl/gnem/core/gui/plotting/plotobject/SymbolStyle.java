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
package llnl.gnem.core.gui.plotting.plotobject;

/**
 * An enumeration of the available Symbol styles. Any new symbols added
 * to the collection must have entries made in this class.
 */
public enum SymbolStyle {
    NONE("NONE"),
    CIRCLE( "Circle" ),
    SQUARE( "Square" ),
    DIAMOND( "Diamond" ),
    TRIANGLEUP( "TriangleUp" ),
    TRIANGLEDN( "TriangleDn" ),
    PLUS( "Plus" ),
    CROSS( "Cross" ),
    STAR5( "Star5" ),
    HEXAGON( "Hexagon" ),
    ERROR_BAR ( "ErrorBar" ),
    ERROR_BAR_SQUARE ( "ErrorBarOnSquare" ),
    ERROR_BAR_TRIANGLE_DN ( "ErrorBarOnTriangleDn" ),
    ERROR_BAR_TRIANGLE_UP ( "ErrorBarOnTriangleUp" );
    private final String myName; // for debug only

    SymbolStyle( String name )
    {
        myName = name;
    }

    public String toString()
    {
        return myName;
    }


    public static SymbolStyle getSymbolStyle( final String name )
    {
        for(SymbolStyle style: SymbolStyle.values()){
           if (style.toString().equals(name)) return style;
        }
       throw new IllegalArgumentException("Not a valid Style");
    }
}
