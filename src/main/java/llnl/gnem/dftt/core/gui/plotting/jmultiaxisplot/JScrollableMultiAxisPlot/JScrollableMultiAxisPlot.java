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
package llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JScrollableMultiAxisPlot;

import llnl.gnem.dftt.core.gui.plotting.DrawingRegion;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JMultiAxisPlot;
import llnl.gnem.dftt.core.gui.plotting.jmultiaxisplot.JSubplot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Stack;
import java.util.prefs.Preferences;

/**
 * User: ganzberger1
 * Date: Aug 16, 2005
 * Time: 2:14:34 PM
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */

/**
 * A class that manages a scrollable JMultiAxisPlot.  Plots may be zoomed in or out as well
 * as a thumbnail viewerJ displayed and managed.
 *
 * @author Michael D. Ganzberger
 * Will not inspect for: MagicNumber
 */
public class JScrollableMultiAxisPlot extends JMultiAxisPlot {
    public static int default_Plot_Height_per_Plot = 200;
    public static int default_Plot_Width_per_Plot = 500;
    public static int default_Plot_Plots_per_Page = 4;
    public static int Max_Plots_per_Page = 30;

    private Stack<ViewData> zoomStack;
    private JScrollPane scrollPane;

    private int plots_per_page = 5;

    private JFrame parentFrame;
    private boolean updatePlot = false;

    private int plot_width_per_plot = 200;
    private int plot_height_per_plot = 100;
    private Rectangle saveBounds = null;


    private boolean scrollingTrigger = true;  // indicates we can accept scrollbar events for thumbnail updates
    private double xfrac = 0;
    private double yfrac = 0;
    private double ratioH = 0;
    private double ratioW = 0;
    int HorizontalScrollBarPosition = 0;
    int VerticalScrollBarPosition = 0;

    public double getRatioH()
    {
        return ratioH;
    }

    public double getRatioW()
    {
        return ratioW;
    }

    public double getXfrac()
    {
        return xfrac;
    }

    public double getYfrac()
    {
        return yfrac;
    }


    public void Render(Graphics g)
    {
        super.Render(g);
    }

    public void Render(Graphics g, double HOffset, double VertOffset, double boxWidth, double boxHeight)
    {
        super.Render(g, HOffset, VertOffset, boxWidth, boxHeight);
    }

    public int getPlots_per_page()
    {
        return plots_per_page;
    }

    public void setPlots_per_page(int plots_per_page)
    {
        this.plots_per_page = plots_per_page;
    }

    public int getPlot_height_per_plot()
    {
        return plot_height_per_plot;
    }

    public void setPlot_height_per_plot(int plot_height_per_plot)
    {
        this.plot_height_per_plot = plot_height_per_plot;
    }

    public int getPlot_width_per_plot()
    {
        return plot_width_per_plot;
    }

    public void setPlot_width_per_plot(int plot_width_per_plot)
    {
        this.plot_width_per_plot = plot_width_per_plot;
    }


    public JScrollableMultiAxisPlot(JFrame owningFrame)
    {
        super();
        parentFrame = owningFrame;
        scrollPane = new JScrollPane(this,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setOpaque(true);
        scrollPane.getHorizontalScrollBar().setOpaque(true);
        scrollPane.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                if (scrollPane.getHorizontalScrollBar().getValue() == HorizontalScrollBarPosition) return;
                HorizontalScrollBarPosition = scrollPane.getHorizontalScrollBar().getValue();
            }
        });
        scrollPane.getVerticalScrollBar().setOpaque(true);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                if (scrollPane.getVerticalScrollBar().getValue() == VerticalScrollBarPosition) return;
                VerticalScrollBarPosition = scrollPane.getVerticalScrollBar().getValue();
            }
        });
        scrollPane.getViewport().revalidate();
        computeIncrements();
        zoomStack = new Stack<ViewData>();


    }

    /**
     * Class to capture the current state used in the zooming process.
     */
    class ViewData {
        Rectangle visibleRect;
        Dimension current;
        int maxX = 0;
        int maxY = 0;

        ViewData(Rectangle rect, Dimension current, int x, int y)
        {
            this.visibleRect = rect;
            this.current = current;
            this.maxX = x;
            this.maxY = y;
        }
    }

    /**
     * For the ScrollableMultiAxisPlot, this means zooming to the visible rectangle not
     * the plotting region like JMultiAxisPlot.
     */
    public void zoomToCurrentBorder()
    {
        Rectangle rect = this.getVisibleRect();
        zoomToBox(rect);
    }

    public void unzoomAll()
    {
        // if (zoomStack.isEmpty()) return;

        // make sure we actually have plots to view...
        int plots = this.getSubplotManager().getSubplots().size();
        if (plots <= 0) return;

        // get the current plot parameters.  These should always be saved after a resize.
        Preferences preferences = Preferences.userRoot().node("llnl/gnem/plotting/jmultiaxisplot/jscrollablemultiaxisplot");
        String text = preferences.get("PLOTWIDTHPERPLOT", String.valueOf(JScrollableMultiAxisPlot.default_Plot_Width_per_Plot));
        int plot_width = Integer.parseInt(text);

        text = preferences.get("PLOTHEIGHTPERPLOT", String.valueOf(JScrollableMultiAxisPlot.default_Plot_Height_per_Plot));
        int plot_height = Integer.parseInt(text);

        String svalue = preferences.get("PLOTPLOTSPERPAGE", String.valueOf(JScrollableMultiAxisPlot.default_Plot_Plots_per_Page));
        int plots_per_page = new Integer(svalue);

        // just in case make sure the max is never exceeded..
        plots_per_page = Math.min(JScrollableMultiAxisPlot.Max_Plots_per_Page, plots_per_page);

        setPlots_per_page(plots_per_page);
        setPlot_height_per_plot(plot_height);
        setPlot_width_per_plot(plot_width);

        // If the zoomstack is not empty, empty it now to unzoom to the original view.
        ViewData vd;
        if (!zoomStack.isEmpty()) {
            while (!zoomStack.isEmpty()) {
                vd = zoomStack.pop();
                this.setPreferredSize(vd.current);
                this.revalidate();
                computeIncrements();
                getScrollPane().revalidate();
            }
        }

        /* Either resize based on new plot parameters or the previously saved size
        */
        if (parentFrame != null) {
            Rectangle frect = parentFrame.getBounds();
            resizePlot((int) frect.getHeight(), (int) frect.getWidth());
        }
        computeIncrements();
        getScrollPane().revalidate();

        this.repaint();
    }

    /**
     * Method to handle a zoom-in into the zoomRect
     *
     * @param zoomRect The boundaries of the region being zoomed in to.
     */
    @Override
    public void zoomToBox(Rectangle zoomRect)
    {
        if (zoomRect.height < 2)
            return;

        DrawingRegion pr = this.getPlotRegion();
        Rectangle currentRect = this.getVisibleRect();
        Dimension current = this.getSize();

        JViewport viewport = scrollPane.getViewport();

        // determine the ratios between the current viewport and the new zoombox
        double hvp = viewport.getHeight();
        double hzr = (double) zoomRect.height;


        double wvp = viewport.getWidth();
        double wzr = (double) zoomRect.width;

        ratioH = hvp / hzr;
        ratioW = wvp / wzr;

        double vertOffset = top /*borderWidth+ VerticalOffset */;
        double horzOffset = left/*borderWidth+ HorizontalOffset*/;

        // calculate the ratio this new zoombox will take up within the plotting region
        // remember that this is the only place where the zoom can occur.
        // Since the x,y begin at 70.70 we remove that to normalize
        xfrac = (zoomRect.getX() - vertOffset) / (pr.getRect().getWidth());
        yfrac = (zoomRect.getY() - horzOffset) / (pr.getRect().getHeight());

//        int plotSpacing = (int) this.getSubplotManager().getplotSpacing();
//        System.out.println("plotSpacing are: "+plotSpacing);
//
//        int ps = unitsMgr.getVertUnitsToPixels(plotSpacing);
        int ps = 0;
        // calculate a new size for the base component on which all the display is placed, JPanel.
        // JPanel-->Border Region-->Plotting Region--ViewPort
        // Since the border is static, we add this without scaling it.
        double Hp_new = (pr.getRect().getHeight()) * ratioH + (2 * vertOffset)
                + ps * getSubplotManager().getSubplots().size();
        double Wp_new = (pr.getRect().getWidth()) * ratioW + (2 * horzOffset) + ps;

        // now, calculate the new size of the plotting region as the base component expanded.
        double Wpr_new = pr.getRect().getWidth() * ratioW;
        double Hpr_new = pr.getRect().getHeight() * ratioH;


        double Xvp_new = (Wpr_new * xfrac) + top;
        double Yvp_new = (Hpr_new * yfrac) + left;

        Dimension d = new Dimension((int) Wp_new, (int) Hp_new);
        this.setMaximumSize(d);
        this.setPreferredSize(d);

        zoomStack.push(new ViewData(currentRect, current, (int) pr.getRect().getWidth(), (int) pr.getRect().getHeight()));

        this.revalidate();

        Rectangle newViewRect = new Rectangle((int) Xvp_new, (int) Yvp_new,
                (int) (zoomRect.width * ratioW), (int) (zoomRect.height * ratioH));

        scrollToLocation(newViewRect);
        computeIncrements();
        this.revalidate();

    }

    public boolean isScrollingTrigger()
    {
        return scrollingTrigger;
    }

    public void setScrollingTrigger(boolean scrollingTrigger)
    {
        this.scrollingTrigger = scrollingTrigger;
    }

    private void scrollToLocation(Rectangle newViewRect)
    {

        setScrollingTrigger(false);
        scrollRectToVisible(newViewRect);
        setScrollingTrigger(true);
    }

    /**
     * Relocate the  visible rectangle to the new x,y coordinates
     *
     * @param newX  starting x-position of the new rectangle.
     * @param newY  starting y-position of the new rectangle.
     */
    public void moveVisibleRectangle(int newX, int newY)

    {
        // Tried to set Rectangle location over and over but finally got repositioning working!!!
        // Warning: scrollRectToVisible may not always do what you think.
        this.scrollPane.getViewport().setViewPosition(new Point(newX, newY));
        this.revalidate();
    }


    /**
     * Calculate and set the new block and unit increments for the H/V scrollbars
     */
    public void computeIncrements()
    {
        int sstep = scrollPane.getHeight();
        int blockH = sstep;
        DrawingRegion pr = this.getPlotRegion();
        if (pr == null || pr.getRect() == null) {
            scrollPane.getHorizontalScrollBar().setBlockIncrement(sstep);
            scrollPane.getVerticalScrollBar().setUnitIncrement(10);
            return;
        }
        Rectangle viewRect = scrollPane.getViewport().getViewRect();

        if ((int) viewRect.getHeight() < blockH)
            blockH = Math.min(pr.getRect().height, (int) viewRect.getHeight());
        if (blockH <= 0) blockH = this.getParent().getHeight();

        int blockW = Math.min(pr.getRect().width, (int) viewRect.getWidth());

        scrollPane.getHorizontalScrollBar().setBlockIncrement(blockH);
        scrollPane.getVerticalScrollBar().setBlockIncrement(blockW);

        scrollPane.getVerticalScrollBar().setUnitIncrement(1);//(int)viewRect.getHeight()/10);
        scrollPane.getHorizontalScrollBar().setUnitIncrement((int) viewRect.getWidth() / 10);
    }

    public JSubplot addSubplot()
    {
        return getSubplotManager().addSubplot(new JSubplot(this, JMultiAxisPlot.XAxisType.Standard));
    }

    /**
     * Return to the previous zoom state. If there is no previous zoom state, then
     * no action is taken.
     */


    @Override
    public boolean zoomOut()
    {

        if (zoomStack.isEmpty()) {
            return true;
        } else {

            ViewData vd = zoomStack.pop();
            this.setPreferredSize(vd.current);
            this.revalidate();

            scrollToLocation(vd.visibleRect);
            this.scrollPane.getViewport().setViewPosition(new Point((int) vd.visibleRect.getX(), (int) vd.visibleRect.getY()));
            computeIncrements();
            this.scrollPane.revalidate();
        }

        return true;
    }

    public void initScrolling(JFrame f)
    {

        parentFrame = f;
        this.updatePlot = true;
        initScrolling();
        this.updatePlot = false;

    }

    public int getTnHeight(int tnwidth, int plotwidth, int plotheight)
    {
        double factor = (double) tnwidth / (double) plotwidth;
        return (int) (factor * (double) plotheight);
    }

    /**
     * This method is called then the PlotSizeOptionsDialog receives a request to update the plot, the thumbnail
     * viewer or both.
     *
     */
    public void initScrolling()
    {
        if (updatePlot) {
            /* Setting this will determine how many plots can fit on a view or page
            */
            unzoomAll();
            return;
        }
        this.repaint();

    }

    /**
     * return true is "zoom in progress" to be used to determine if we should resize or not
     * and recalculate the plot size.
     *
     * @return zoominprogress
     */
    public boolean zoomInProgress()
    {
        return !zoomStack.isEmpty();

    }

    /**
     * As the user resizes the "parent" frame so to the plot width and height need to be adjusted to agree with
     * the desired "plots per page" where a page is the visible rectangle.  Once calculated the new parameters are
     * saved and applied.
     *
     * @param parentHeight  The height in pixels of the parent frame.
     * @param parentWidth    The width in pixels of the parent frame.
     */
    public void resizePlot(int parentHeight, int parentWidth)
    {
        if (zoomInProgress()) return;  // don't resize plots if we are zooming...

        Preferences preferences = Preferences.userRoot().node("llnl/gnem/plotting/jmultiaxisplot/jscrollablemultiaxisplot");

        String svalue = preferences.get("PLOTPLOTSPERPAGE", String.valueOf(JScrollableMultiAxisPlot.default_Plot_Plots_per_Page));
        Integer V = new Integer(svalue);
        if ((V <= 0) || (V > JScrollableMultiAxisPlot.Max_Plots_per_Page))
            V = new Integer(String.valueOf(JScrollableMultiAxisPlot.default_Plot_Plots_per_Page));
        int desired_Plots_Per_Page = V;

        // make sure we do not exceed the number of plots that are possible.
        int plots = this.getSubplotManager().getSubplots().size();
        int plots1 = plots;
        if (plots1 <= 0) plots1 = desired_Plots_Per_Page;

        desired_Plots_Per_Page = Math.min(desired_Plots_Per_Page, plots1);

        int plotSpacing = (int) this.getSubplotManager().getplotSpacing();
        int ps = unitsMgr.getVertUnitsToPixels(plotSpacing);


        Rectangle pr = getPlotRegion().getRect();
        // if the plot region rectangle is not set yet then set it here to a default.
        if (pr == null) {
            getPlotRegion().setRect(48, 48,
                    Math.max(735, parentHeight - 209),
                    Math.max(1132, parentWidth - 108));  // x,y,height, width
            pr = getPlotRegion().getRect();
        }

        // now set the new parametes
        // subtracting 26 from the width keeps the scroll bar in proper proportion to the width
        // otherwise it appears we always want to scroll a little on the horizontal
        double new_Single_Plot_Height = (parentHeight - pr.getY() * 2 - (ps * desired_Plots_Per_Page)) / (double) desired_Plots_Per_Page;
        double new_Single_Plot_Width = parentWidth - 26 - pr.getX() * 2; //pr.getWidth();

        setPreferredSize(new Dimension((int) ((new_Single_Plot_Width + pr.getX() * 2)),    // -20
                (int) ((double) plots * new_Single_Plot_Height) + (ps * plots) +
                        (int) pr.getY()));

        getScrollPane().setSize((int) (new_Single_Plot_Width + pr.getX() * 2),
                (int) (pr.getY() + new_Single_Plot_Height * desired_Plots_Per_Page + (ps * desired_Plots_Per_Page)));

        preferences.put("PLOTHEIGHTPERPLOT", String.valueOf((int) new_Single_Plot_Height));
        preferences.put("PLOTWIDTHPERPLOT", String.valueOf((int) new_Single_Plot_Width));
    }

    public JScrollPane getScrollPane()
    {
        return scrollPane;
    }


    public Rectangle getSaveBounds()
    {
        return saveBounds;
    }

}
