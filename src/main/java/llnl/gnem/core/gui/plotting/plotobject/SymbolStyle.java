/**
 * User: dodge1
 * Date: Mar 12, 2004
 * Time: 2:40:41 PM
 * To change this template use File | Settings | File Templates.
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
