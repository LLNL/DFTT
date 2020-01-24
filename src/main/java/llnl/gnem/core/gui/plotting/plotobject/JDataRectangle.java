package llnl.gnem.core.gui.plotting.plotobject;

import java.awt.geom.Point2D;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Mar 22, 2006
 */
public class JDataRectangle extends JPolygon {

    public JDataRectangle( double xmin, double ymin, double width, double height )
    {
        Point2D[] vertices = {new Point2D.Double( xmin, ymin ),
                              new Point2D.Double( xmin, ymin + height ),
                              new Point2D.Double( xmin + width, ymin + height ),
                              new Point2D.Double( xmin + width, ymin ),
                              new Point2D.Double( xmin, ymin )};
        this.setVertices( vertices );
    }
}
