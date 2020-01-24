/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.plotting.contour;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import llnl.gnem.core.gui.plotting.JBasicPlot;
import llnl.gnem.core.gui.plotting.plotobject.DataText;
import llnl.gnem.core.gui.plotting.plotobject.JDataRectangle;
import llnl.gnem.core.gui.plotting.plotobject.Line;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;

/**
 *
 * @author dodge1
 */
public class ContourLine extends Line {
private final ContourValue value;
private boolean renderLabel=false;

    public ContourLine(Contour contour) {
        super(contour.getXarray(), contour.getYarray());
        value = contour.getContourValue();
    }

    public double getValue()
    {
        return value.getZ();
    }

    public String getLabel() {
    	return value.getLabel();
    }

    public void setRenderLabel(boolean render) {
    	renderLabel = render;
    }

    /**
     * render the line to the supplied graphics context
     *
     * @param g The graphics context
     * @param owner The JSubplot that owns this Line
     */
    @Override
    public synchronized void render(Graphics g, JBasicPlot owner) {
        super.render(g, owner);
        //Add specialized rendering code here...

        if (renderLabel) {
        	float[] xvals = getXdata();
        	float[] yvals = getYdata();

            double x = getXdata()[0];
            double y = getYdata()[0];

            double ymid = (getLineBounds().ymax + getLineBounds().ymin) / 2.0;
            double closest = Math.abs(ymid-y);
            for (int ndx=1; ndx < yvals.length; ++ndx ) {
            	double dist = Math.abs(ymid - yvals[ndx]);
            	if (dist < closest) {
            		closest = dist;
            		y = yvals[ndx];
            		x = xvals[ndx];
            	}
            }
            x+=Math.abs(x*0.0005); // offset a little by value

            Color color = g.getColor();

            System.out.println("Render contour label at("+x+","+y+")");
            System.out.flush();

        	DataText text = new DataText(x, y, value.getLabel());
        	text.setColor(color);
        	text.setFontSize(text.getFontSize()*1.5);
        	text.render(g, owner);

        	Rectangle shape = text.getBounds();
            Coordinate minCoord = new Coordinate( shape.getMinX(), shape.getMinY() );
            coordTransform.PlotToWorld(minCoord);

            JDataRectangle rec = new JDataRectangle(minCoord.getX(), minCoord.getY(), shape.width, shape.height);
        	rec.setEdgeColor(color);
        	rec.setFillColor(Color.gray);
        	rec.render(g, owner);
        }
    }
}
