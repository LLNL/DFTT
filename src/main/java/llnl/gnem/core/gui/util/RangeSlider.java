package llnl.gnem.core.gui.util;

import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Created by dodge1
 * Date: Apr 8, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */

/**
 * An extension of JSlider to select a range of values using two thumb controls.
 * The thumb controls are used to select the lower and upper value of a range
 * with pre-determined minimum and maximum values.
 * <p></p>
 * <p>RangeSlider makes use of the default BoundedRangeModel, which supports an
 * inner range defined by a value and an extent.  The upper value returned by
 * RangeSlider is simply the lower value plus the extent.</p>
 *
 * @author Ernie Yu, LimeWire LLC
 */
public class RangeSlider extends JSlider {
    private RangeSliderUI uidelegate;

    /**
     * Constructs a RangeSlider with default minimum and maximum values of 0
     * and 100.
     */
    public RangeSlider() {

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (uidelegate != null && uidelegate.isUpperThumbSelected()) {
                    if (e.getWheelRotation() < 0)setUpperValue(getUpperValue() + 1);
                    else setUpperValue(getUpperValue() - 1);
                } else {
                    if (e.getWheelRotation() < 0) setValue(getValue() + 1);
                    else setValue(getValue() - 1);
                }
            }
        });

    }

    /**
     * Constructs a RangeSlider with the specified default minimum and maximum
     * values.
     *
     * @param min The minimum value allowable with this slider.
     * @param max The maximum value obtainable with this slider.
     */
    public RangeSlider(int min, int max) {
        super(min, max);
    }

    /**
     * Overrides the superclass method to install the UI delegate to draw two
     * thumbs.
     */
    @Override
    public void updateUI() {
        uidelegate = new RangeSliderUI(this);
        setUI(uidelegate);
        // Update UI for slider labels.  This must be called after updating the
        // UI of the slider.  Refer to JSlider.updateUI().
        updateLabelUIs();
    }

    /**
     * Returns the lower value in the range.
     * @return 
     */
    @Override
    public int getValue() {
        return super.getValue();
    }

    /**
     * Sets the low
     * @param value value in the range.
     */
    @Override
    public void setValue(int value) {
        int oldValue = getValue();
        if (oldValue == value) {
            return;
        }

        // Compute new value and extent to maintain upper value.
        int oldExtent = getExtent();
        int newValue = Math.min(Math.max(getMinimum(), value), oldValue + oldExtent);
        int newExtent = oldExtent + oldValue - newValue;

        // Set new value and extent, and fire a single change event.
        getModel().setRangeProperties(newValue, newExtent, getMinimum(),
                getMaximum(), getValueIsAdjusting());
    }

    /**
     * Returns the upper value in the range.
     *
     * @return The upper value of this slider.
     */
    public int getUpperValue() {
        return getValue() + getExtent();
    }

    /**
     * Sets the upper value in the range.
     *
     * @param value The value to which the slider will be set.
     */
    public void setUpperValue(int value) {
        // Compute new extent.
        int lowerValue = getValue();
        int newExtent = Math.min(Math.max(0, value - lowerValue), getMaximum() - lowerValue);

        // Set extent to set upper value.
        setExtent(newExtent);
    }
}
