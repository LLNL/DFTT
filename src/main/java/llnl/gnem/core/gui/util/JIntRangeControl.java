package llnl.gnem.core.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;

public class JIntRangeControl extends JPanel implements JRangeControl, PropertyChangeListener {
    private final JFormattedTextField minField;
    private final JFormattedTextField maxField;
    private final Collection<PropertyChangeListener> listeners;

    /**
     * Constructs an instance of the JDoubleRangeControl.
     *
     * @param valueName The name of the parameter whose range is being managed
     * here.
     * @param minvalue The minimum value this parameter can have. This will be
     * enforced by the control.
     * @param maxvalue The maximum value the parameter can have. This is
     * enforced by the control.
     * @param columns The number of columns to set the JTextField controls
     * (width in columns).
     */
    public JIntRangeControl(String valueName, int minvalue, int maxvalue, int columns) {
        super(new SpringLayout());

        listeners = new ArrayList<PropertyChangeListener>();
        setBorder(BorderFactory.createTitledBorder(valueName));
        minField = new JFormattedTextField(minvalue);
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

        maxField = new JFormattedTextField(maxvalue);
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
        
        final AbstractFormatter minFormatter = new InfinityFormatter(Integer.MIN_VALUE);
        minField.setFormatterFactory(new AbstractFormatterFactory() {
            @Override
            public AbstractFormatter getFormatter(JFormattedTextField jftf) {
                return minFormatter;
            }
        });

        final AbstractFormatter maxFormatter = new InfinityFormatter(Integer.MAX_VALUE);
        maxField.setFormatterFactory(new AbstractFormatterFactory() {
            @Override
            public AbstractFormatter getFormatter(JFormattedTextField jftf) {
                return maxFormatter;
            }
        });
    }

    public void addValueChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

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

    public int getMinValue() {
        return (Integer) minField.getValue();
    }

    public int getMaxValue() {
        return (Integer) maxField.getValue();
    }

    public void setMinMaxValues(final int min, final int max) {
        if (min > max) {
            throw new IllegalArgumentException("Min value is > max value. Cannot set values.");
        }

        minField.setValue(min);
        maxField.setValue(max);
    }

    @Override
    public boolean ValidateChange() {
        return (Integer) minField.getValue() <= (Integer) maxField.getValue();
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        for (PropertyChangeListener listener : listeners) {
            if (listener != this) {
                listener.propertyChange(pce);
            }
        }
    }
    
    private static class InfinityFormatter extends AbstractFormatter {
        private final int inf;
        
        public InfinityFormatter(int inf) {
            this.inf = inf;
        }
        
        @Override
        public Object stringToValue(String string) throws ParseException {
            if (string.isEmpty()) {
                return inf;
            } else {
                return Integer.parseInt(string);
            }
        }

        @Override
        public String valueToString(Object o) throws ParseException {
            Integer i = (Integer) o;
            if (i == inf) {
                return "";
            } else {
                return i + "";
            }
        }
    }
}
