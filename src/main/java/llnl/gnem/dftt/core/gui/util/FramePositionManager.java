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
package llnl.gnem.dftt.core.gui.util;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Created by: dodge1
 * Date: Jul 15, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */

/**
 * FramePositionManager is a class that manages size and location persistence for a JFrame.
 * Using JFrame descendents use this class by adding an instance as a ComponentListener.
 */
@SuppressWarnings({"FieldMayBeFinal"})
public class FramePositionManager implements ComponentListener {
    private boolean initialWindowBoundsSet;
    private final Preferences preferences;
    private final JFrame frame;

    private final Map<JSplitPane, String> splitters;
    private final SplitterPropertyChangeListener splitterListener;

    /**
     * Constructs an instance of FramePositionManager
     *
     * @param node          The Preferences node where this JFrame's position information is to be stored.
     * @param frame         The instance of the JFrame that is to be managed.
     * @param initialLeft   The default left position of the JFrame.
     * @param initialTop    The default top position of the JFrame.
     * @param initialWidth  The default width of the JFrame.
     * @param initialHeight The default Height of the JFrame.
     */
    public FramePositionManager(final String node, final JFrame frame, final int initialLeft,
                                final int initialTop, final int initialWidth, final int initialHeight) {
        initialWindowBoundsSet = false;
        preferences = Preferences.userRoot().node(node);
        this.frame = frame;
        setFrameBounds(initialLeft, initialTop, initialWidth, initialHeight, frame);
        initialWindowBoundsSet = true;
        splitters = new HashMap<JSplitPane, String>();
        splitterListener = new SplitterPropertyChangeListener();
    }

    private void setFrameBounds(final int initialLeft, final int initialTop, final int initialWidth, final int initialHeight, final JFrame frame) {
        int left = Integer.parseInt(preferences.get("LEFT", Integer.toString(initialLeft)));
        int top = Integer.parseInt(preferences.get("TOP", Integer.toString(initialTop)));
        int width = Integer.parseInt(preferences.get("WIDTH", Integer.toString(initialWidth)));
        int height = Integer.parseInt(preferences.get("HEIGHT", Integer.toString(initialHeight)));
        frame.setBounds(left, top, width, height);
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
        if (!initialWindowBoundsSet || !frame.isVisible())
            return;
        Integer left = frame.getX();
        Integer top = frame.getY();
        preferences.put("LEFT", left.toString());
        preferences.put("TOP", top.toString());
    }

    public void componentResized(ComponentEvent e) {
        if (!initialWindowBoundsSet ||!frame.isVisible())
            return;
        Integer width = frame.getWidth();
        Integer height = frame.getHeight();
        preferences.put("WIDTH", width.toString());
        preferences.put("HEIGHT", height.toString());
    }

    public void componentShown(ComponentEvent e) {
    }


    public void registerSplitter( JSplitPane splitter, String name, int initialPosition )
    {
        splitter.addPropertyChangeListener(splitterListener);
        int position = preferences.getInt(name, initialPosition);
        splitter.setDividerLocation(position);
        splitters.put(splitter,name);
    }

    private class SplitterPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent changeEvent) {
            JSplitPane sourceSplitPane = (JSplitPane) changeEvent.getSource();
            String propertyName = changeEvent.getPropertyName();
            if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
                int current = sourceSplitPane.getDividerLocation();
                String name = splitters.get(sourceSplitPane);
                if( name != null ){
                    preferences.putInt(name, current);
                }
            }
        }

    }


}

