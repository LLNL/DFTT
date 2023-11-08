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

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * A class that allows interactive specification and editing of a value range
 * for some parameter represented as a double. The class handles display,
 * validation, setting and getting the range of the parameter. Visually the
 * control consists of two labeled TextFields surrounded by a titled border with
 * the title consisting of the parameter name.
 */
public class JDoubleRangeControl extends JPanel implements JRangeControl, PropertyChangeListener {

    private static final long serialVersionUID = 1L;
	private final JFormattedTextField minField;
    private final JFormattedTextField maxField;
    // The edit formatters do the heavy lifting for validating user input
    private final NumberFormatter minEditFormatter;
    private final NumberFormatter maxEditFormatter;
    private final Collection<PropertyChangeListener> listeners;

    
    
    
    /**
     * Constructs an instance of the JDoubleRangeControl.
     *
     * @param valueName The name of the parameter whose range is being managed here.
     * @param minvalue The minimum value this parameter can have. This will be enforced by the control.
     * @param maxvalue The maximum value the parameter can have. This is enforced by the control.
     * @param columns The number of columns to set the JTextField controls (width in columns).
     */
	public JDoubleRangeControl(final String valueName, final double minvalue, double maxvalue, final int columns) {
		super(new SpringLayout());
		listeners = new ArrayList<>();
		setBorder(BorderFactory.createTitledBorder(valueName));

		NumberFormatter minDefaultFormatter = new NumberFormatter(new DecimalFormat("#.#"));
		NumberFormatter minDisplayFormatter = new NumberFormatter(new DecimalFormat("#,###.#"));
		minEditFormatter = new NumberFormatter(new DecimalFormat("#.#"));
		minDefaultFormatter.setValueClass(Double.class);
		minDisplayFormatter.setValueClass(Double.class);
		minEditFormatter.setValueClass(Double.class);
		//for the editor formatter set initial min/max
		minEditFormatter.setMinimum(minvalue);
		minEditFormatter.setMaximum(maxvalue);
		DefaultFormatterFactory minFormatFactory = new DefaultFormatterFactory(minDefaultFormatter, minDisplayFormatter,
		        minEditFormatter);
		
		minField = new JFormattedTextField(minFormatFactory, minvalue);
		minField.setColumns(columns);
		minField.setPreferredSize(new Dimension(50, 20));
		minField.addPropertyChangeListener("value", this);
		JLabel label = new JLabel("Min", JLabel.TRAILING);
		label.setLabelFor(minField);
		add(label);
		add(minField);

		JLabel spacer = new JLabel();
		spacer.setPreferredSize(new Dimension(40, 20));
		add(spacer);

		NumberFormatter maxDefaultFormatter = new NumberFormatter(new DecimalFormat("#.#"));
		NumberFormatter maxDisplayFormatter = new NumberFormatter(new DecimalFormat("#,###.#"));
		maxEditFormatter = new NumberFormatter(new DecimalFormat("#.#"));
		maxDefaultFormatter.setValueClass(Double.class);
		maxDisplayFormatter.setValueClass(Double.class);
		maxEditFormatter.setValueClass(Double.class);
		//for the editor formatter set initial min/max
		maxEditFormatter.setMinimum(minvalue);
		maxEditFormatter.setMaximum(maxvalue);
		DefaultFormatterFactory maxFormatFactory = new DefaultFormatterFactory(maxDefaultFormatter, maxDisplayFormatter,
		        maxEditFormatter);
		
		maxField = new JFormattedTextField(maxFormatFactory, maxvalue);
		maxField.setColumns(columns);
		maxField.setPreferredSize(new Dimension(50, 20));
		maxField.addPropertyChangeListener("value", this);
		label = new JLabel("Max", JLabel.TRAILING);
		label.setLabelFor(maxField);
		add(label);
		add(maxField);

		final int numRows = 1;
		final int numColumns = 5;
		SpringUtilities.makeCompactGrid(this, numRows, numColumns, 10, 0, 10, 0);

	}
        
    
    public JDoubleRangeControl(final String valueName, final double minAllowableValue, 
             double maxAllowableValue, 
            final double minValue, 
            final double maxValue, 
            final int columns) {
        this(valueName,minAllowableValue,maxAllowableValue, columns);
        minField.setValue(minValue);
        maxField.setValue(maxValue);
    }
        
        

    public void addValueChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Sets the background color of this control.
     *
     * @param color The new background color.
     */
    @Override
    public void setBackground(Color color) {
        if (minField != null) {
            minField.setBackground(color);
        }
        if (maxField != null) {
            maxField.setBackground(color);
        }
        super.setBackground(color);
    }

    /**
     * Gets the minimum value of the parameter managed by this control.
     *
     * @return The minimum value of the parameter.
     */
    public double getMinValue() {
        return (Double) minField.getValue();
    }

    /**
     * Gets the maximum value of the parameter managed by this control.
     *
     * @return The maximum value of the parameter.
     */
    public double getMaxValue() {
        return (Double) maxField.getValue();
    }

    /**
     * Simultaneously sets the minimum and maximum values of this parameter.
     *
     * @param min The minimum value of the parameter.
     * @param max The maximum value of the parameter.
     */
    public void setMinMaxValues(final double min, final double max) {
        if (min > max) {
            throw new IllegalArgumentException("Min value is > max value. Cannot set values.");
        }

        minField.setValue(min);
        maxField.setValue(max);
        minEditFormatter.setMaximum(max);
        maxEditFormatter.setMinimum(min);
    }

    @Override
    public boolean ValidateChange() {
        return (Double) minField.getValue() <= (Double) maxField.getValue();
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        // handle the updates to this classes formatters
    	Object source = pce.getSource();
        if (source == minField) {
            double min = ((Number)minField.getValue()).doubleValue();
            maxEditFormatter.setMinimum(min);
        } else if (source == maxField) {
        	 double max = ((Number)maxField.getValue()).doubleValue();
        	 minEditFormatter.setMaximum(max);
        }
    	// for other listeners, let them know that there was a value change
    	for (PropertyChangeListener listener : listeners) {
            if (listener != this) {
                listener.propertyChange(pce);
            }
        }
    }
}
