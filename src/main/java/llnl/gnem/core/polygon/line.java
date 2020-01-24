package llnl.gnem.core.polygon;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * User: dodge1
 * Date: Jan 22, 2004
 * Time: 1:43:24 PM
 * To change this template use Options | File Templates.
 */
public class line {
    public Vector3D p1;
    public Vector3D p2;

    public line()
    {
        p1 = new Vector3D( 0.0, 0.0, 0.0 );
        p2 = new Vector3D( 0.0, 0.0, 0.0 );
    }

    public line( Vector3D pp1, Vector3D pp2 )
    {
        p1 = new Vector3D( pp1.getX(), pp1.getY(), pp1.getZ() );
        p2 = new Vector3D( pp2.getX(), pp2.getY(), pp2.getZ() );
    }

}


