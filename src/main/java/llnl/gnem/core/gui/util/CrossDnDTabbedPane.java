package llnl.gnem.core.gui.util;

/**
 * Modified CrossDnDTabbedPane.java
 * http://java-swing-tips.blogspot.com/2008/04/drag-and-drop-tabs-in-jtabbedpane.html
 * originally written by Terai Atsuhiro. so that tabs can be transfered from one
 * pane to another. eed3si9n.
 */
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class CrossDnDTabbedPane extends JTabbedPane {
    private int sourceIndex;
    private boolean dragging;
    
    private final GhostGlassPane glassPane;
    private final MouseMotionListener motionListener = new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent me) {
            dragging = true;
            glassPane.drag();
        }

        @Override
        public void mouseMoved(MouseEvent me) {
        }
        
    };
    private final MouseListener mouseListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent me) {
        }

        @Override
        public void mousePressed(MouseEvent me) {
            Point tabPt = me.getPoint();
            sourceIndex = indexAtLocation(tabPt.x, tabPt.y);
            
            glassPane.initDrag(CrossDnDTabbedPane.this, sourceIndex);
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if (dragging) {
                glassPane.endDrag();
            }
            dragging = false;
        }

        @Override
        public void mouseEntered(MouseEvent me) {
        }

        @Override
        public void mouseExited(MouseEvent me) {
        }
        
    };
    
    public CrossDnDTabbedPane() {
        sourceIndex = -1;
        dragging = false;
        
        glassPane = GhostGlassPane.getInstance();
        glassPane.addListener(this);
        
        addMouseMotionListener(motionListener);
        addMouseListener(mouseListener);
    }
    
    
    public void convertTab(int sourceIndex, CrossDnDTabbedPane target, int targetIndex) {
        if (sourceIndex < 0) {
            return;
        }

        Component cmp = getComponentAt(sourceIndex);
        String str = getTitleAt(sourceIndex);

        if (targetIndex < 0 || target == null) {
            handleNoTarget(this, sourceIndex);
            return;
        }

        Icon icon = getIconAt(sourceIndex);
        String tip = getToolTipTextAt(sourceIndex);

        remove(sourceIndex);
        target.insertTab(str, icon, cmp, tip, targetIndex);
        target.setSelectedIndex(targetIndex);
    }  
    
    public int getTargetTabIndex(Point a_point) {
        boolean isTopOrBottom = getTabPlacement() == JTabbedPane.TOP
                || getTabPlacement() == JTabbedPane.BOTTOM;

        // if the pane is empty, the target index is always zero.
        if (getTabCount() == 0) {
            return 0;
        }

        for (int i = 0; i < getTabCount(); i++) {
            Rectangle r = getBoundsAt(i);
            if (isTopOrBottom) {
                r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
            } else {
                r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
            }

            if (r.contains(a_point)) {
                return i;
            }
        }

        Rectangle r = getBoundsAt(getTabCount() - 1);
        if (isTopOrBottom) {
            int x = r.x + r.width / 2;
            r.setRect(x, r.y, getWidth() - x, r.height);
        } else {
            int y = r.y + r.height / 2;
            r.setRect(r.x, y, r.width, getHeight() - y);
        }

        return r.contains(a_point) ? getTabCount() : -1;
    }
    
    protected void handleNoTarget(CrossDnDTabbedPane source, int sourceIndex) {
    }

    /*
    public void initTargetLeftRightLine(int next, CrossDnDTabbedPane.TabTransferData a_data) {
        if (next < 0) {
            m_lineRect.setRect(0, 0, 0, 0);
            setLineVisible(false);
            return;
        } // if

        if ((a_data.getTabbedPane() == this)
                && (a_data.getTabIndex() == next
                || next - a_data.getTabIndex() == 1)) {
            m_lineRect.setRect(0, 0, 0, 0);
            setLineVisible(false);
        } else if (getTabCount() == 0) {
            m_lineRect.setRect(0, 0, 0, 0);
            setLineVisible(false);
        } else if (next == 0) {
            Rectangle rect = getBoundsAt(0);
            m_lineRect.setRect(rect.x - LINEWIDTH / 2, rect.y, LINEWIDTH, rect.height);
            setLineVisible(true);
        } else if (next == getTabCount()) {
            Rectangle rect = getBoundsAt(getTabCount() - 1);
            m_lineRect.setRect(rect.x + rect.width - LINEWIDTH / 2, rect.y,
                    LINEWIDTH, rect.height);
            setLineVisible(true);
        } else {
            Rectangle rect = getBoundsAt(next - 1);
            m_lineRect.setRect(rect.x + rect.width - LINEWIDTH / 2, rect.y,
                    LINEWIDTH, rect.height);
            setLineVisible(true);
        }
    }

    public void initTargetTopBottomLine(int next, CrossDnDTabbedPane.TabTransferData a_data) {
        if (next < 0) {
            m_lineRect.setRect(0, 0, 0, 0);
            setLineVisible(false);
            return;
        } // if

        if ((a_data.getTabbedPane() == this)
                && (a_data.getTabIndex() == next
                || next - a_data.getTabIndex() == 1)) {
            m_lineRect.setRect(0, 0, 0, 0);
            setLineVisible(false);
        } else if (getTabCount() == 0) {
            m_lineRect.setRect(0, 0, 0, 0);
            setLineVisible(false);
            return;
        } else if (next == getTabCount()) {
            Rectangle rect = getBoundsAt(getTabCount() - 1);
            m_lineRect.setRect(rect.x, rect.y + rect.height - LINEWIDTH / 2,
                    rect.width, LINEWIDTH);
            setLineVisible(true);
        } else if (next == 0) {
            Rectangle rect = getBoundsAt(0);
            m_lineRect.setRect(rect.x, -LINEWIDTH / 2, rect.width, LINEWIDTH);
            setLineVisible(true);
        } else {
            Rectangle rect = getBoundsAt(next - 1);
            m_lineRect.setRect(rect.x, rect.y + rect.height - LINEWIDTH / 2,
                    rect.width, LINEWIDTH);
            setLineVisible(true);
        }
    }*/
}

class GhostGlassPane extends JWindow {
    public static final long serialVersionUID = 1L;
    
    private static GhostGlassPane instance = null;
    private Rectangle rect = null;
    private CrossDnDTabbedPane source;
    private int sourceIndex;
    private CrossDnDTabbedPane target;
    private int targetIndex;
    private final Collection<CrossDnDTabbedPane> paneListeners;

    private GhostGlassPane() {
        setFocusable(false);
        setEnabled(false);
        enableInputMethods(false);

        source = null;
        sourceIndex = -1;
        target = null;
        targetIndex = -1;

        paneListeners = new ArrayList<CrossDnDTabbedPane>();
    }

    public void addListener(CrossDnDTabbedPane pane) {
        paneListeners.add(pane);
    }
        
    public void initDrag(CrossDnDTabbedPane source, int sourceIndex) {
        if (sourceIndex >= 0) {
            this.source = source;
            this.sourceIndex = sourceIndex;
            setRect(source.getBoundsAt(sourceIndex));
        }
    }
    
    public void drag() {
        updateLocation();
        if (!isVisible()) {
            setVisible(true);
        }        
    }
    
    public void endDrag() {
        transfer(source, sourceIndex);
        
        setRect(null);
        clearTarget();
        
        setVisible(false);
    }

    private void setRect(Rectangle rect) {
        this.rect = rect;
        if (rect != null) {
            setSize(rect.width, rect.height);
            updateFillColor();
        }
    }

    private void transfer(CrossDnDTabbedPane source, int sourceIndex) {
        if (validDrop()) {
            source.convertTab(sourceIndex, target, targetIndex);
        }
    }

    private void updateLocation() {
        Point screenLocation = new Point(MouseInfo.getPointerInfo().getLocation());
        if (rect != null) {
            screenLocation.translate(-rect.width / 2, -rect.height / 2);
        }
        setLocation(screenLocation);
        dragOver(screenLocation);
        repaint();
    }
    
    private void dragOver(Point screenLocation) {
        boolean inBounds = false;
        for (CrossDnDTabbedPane pane : paneListeners) {
            Point panePoint = new Point(screenLocation);
            SwingUtilities.convertPointFromScreen(panePoint, pane);
            if (pane.isVisible() && pane.contains(panePoint)) {
                target = pane;
                
                int baseIndex = target.getTargetTabIndex(panePoint);
                targetIndex = sourceIndex > baseIndex || source != target ? baseIndex : baseIndex - 1;            
                
                /*
                if (target.getTabPlacement() == JTabbedPane.TOP || target.getTabPlacement() == JTabbedPane.BOTTOM) {
                    target.initTargetLeftRightLine(targetIndex, data);
                } else {
                    target.initTargetTopBottomLine(targetIndex, data);
                }*/
                
                inBounds = true;
                break;
            }
        }
        
        if (!inBounds) {
            clearTarget();
        }
        
        updateFillColor();
    }
    
    private void clearTarget() {
        target = null;
        targetIndex = -1;
    }

    private void updateFillColor() {
        Color fillColor = Color.BLUE;
        if (targetIndex >= 0) {
            if (validDrop()) {
                fillColor = Color.CYAN;
            }
        }
              
        setForeground(fillColor);
        setBackground(fillColor);
    }
    
    private boolean validDrop() {
        return source != null && (source != target || sourceIndex != targetIndex);
    }

    public static GhostGlassPane getInstance() {
        if (instance == null) {
            instance = new GhostGlassPane();
        }
        return instance;
    }
}