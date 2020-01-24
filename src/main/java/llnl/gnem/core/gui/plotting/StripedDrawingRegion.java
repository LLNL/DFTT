package llnl.gnem.core.gui.plotting;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import llnl.gnem.core.gui.plotting.jmultiaxisplot.YAxis;
import llnl.gnem.core.gui.plotting.transforms.Coordinate;
import llnl.gnem.core.gui.plotting.transforms.CoordinateTransform;

/**
 *
 * @author addair1
 */
public class StripedDrawingRegion extends DrawingRegion {

    private final YAxis axis;

    public StripedDrawingRegion(YAxis axis) {
        this.axis = axis;
        //stripeColor = new Color(0xf8f8ff); // ghost white
    }

    @Override
    public void render(Graphics g) {
        super.render(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(getOffsetColor(7));

        CoordinateTransform ct = axis.getCoordinateTransform();

        Rectangle box = getRect();
        TickMetrics ticks = axis.getTickMetrics(box.height);
        boolean skip = true;
        int last = 0;
        boolean first = true;
        while (ticks.hasNext()) {
            double value = ticks.getNext();
            Coordinate c = new Coordinate(0.0, 0.0, 0.0, value);
            ct.WorldToPlot(c);
            int y = (int) c.getY();

            if (first) {
                first = false;
            } else {
                if (skip) {
                    last = y;
                } else {
                    g2d.fillRect(box.x, y, box.width, last - y);
                }
                skip = !skip;
            }
        }

        drawBox(g);
    }
}
