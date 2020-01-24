/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.plotting.contour;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Contour {

    private final transient List<Point> data = new ArrayList<Point>();
    private ContourValue cValue;
    private boolean finished = false;
    private boolean closed = false;

    public Contour(double z, String label) {
        cValue = new ContourValue(z, label);
    }

    public void addPoint(Point pt) {
        data.add(pt);
    }

    public ContourValue getContourValue() {
        return cValue;
    }

    public Point getLastPoint() {
        return data.get(data.size() - 1);
    }

    public List<Point> getData() {
        return data;
    }

    public int getNumberOfPoints() {
        return data.size();
    }

    public float[] getXarray()
    {
        float[] result = new float[data.size()];
        for( int j = 0; j < data.size(); ++j ){
            result[j] = (float)data.get(j).getX();
        }
        return result;
    }

    public float[] getYarray()
    {
        float[] result = new float[data.size()];
        for( int j = 0; j < data.size(); ++j ){
            result[j] = (float)data.get(j).getY();
        }
        return result;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setClosed(final boolean closed) {
        this.closed = closed;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }

    public void outputOsm(final long wayid, final long startid, final boolean major, final PrintStream s, final boolean nodes, final boolean way) {
        int size = data.size();
        if (isClosed()) {
            size--; // repeat first node at the way end
        }
        if (data.isEmpty() || size < 2) {
            return;
        }
        if (nodes) {
            long id = startid;
            for (int i = 0; i < size; i++) {
                s.println("<node id=\"" + id + "\" lat=\"" + data.get(i).getX() + "\" lon=\"" + data.get(i).getY() + "\" />");
                id++;
            }
        }
        if (way) {
            long id = startid;
            s.println("<way id=\"" + wayid + "\">");
            for (int i = 0; i < size; i++) {
                s.println("<nd ref=\"" + id + "\" />");
                id++;
            }
            if (isClosed()) { // last node equals to the first one
                s.println("<nd ref=\"" + startid + "\" />");
            }
            s.println("<tag k=\"ele\" v=\"" + cValue.getZ() + "\" />");
            s.println("<tag k=\"contour\" v=\"elevation\" />");
            if (major) {
                s.println("<tag k=\"contour_ext\" v=\"elevation_major\" />");
            } else {
                s.println("<tag k=\"contour_ext\" v=\"elevation_minor\" />");
            }
            s.println("</way>");
        }
    }
}
