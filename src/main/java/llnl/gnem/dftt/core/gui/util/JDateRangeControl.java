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

import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.TimeT;

import javax.swing.*;
import java.awt.*;

/**
 * Created by: dodge1
 * Date: Jul 6, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */


/**
 * A class that displays and allows editing of a minimum and a
 * maximum time. The two times are represented using JDateField
 * objects. The minimum and maximum times can be formatted
 * independently. No checking is done to ensure that the minimum
 * time is, in fact less than the maximum time.
 * The minimum and maximum times can be set and changed individually
 * or simultaneously.
 */
public class JDateRangeControl extends JPanel {

    public JDateRangeControl()
    {
        super( new SpringLayout() );


        final int panelWidth = 300;
        final int panelHeight = 45;

        minDate = new JDateField();
        maxDate = new JDateField();

        JPanel minPanel = new JPanel();
        JLabel minLabel = new JLabel( "Minimum", JLabel.TRAILING );
        minLabel.setLabelFor( minDate );
        minPanel.add( minLabel );
        minPanel.add( minDate );
        minPanel.setMinimumSize( new Dimension( panelWidth, panelHeight ) );
        minPanel.setMaximumSize( new Dimension( panelWidth, panelHeight ) );
        minPanel.setPreferredSize( new Dimension( panelWidth, panelHeight ) );

        JPanel maxPanel = new JPanel();
        JLabel maxLabel = new JLabel( "Maximum", JLabel.TRAILING );
        maxLabel.setLabelFor( minDate );
        maxPanel.add( maxLabel );
        maxPanel.add( maxDate );
        maxPanel.setMinimumSize( new Dimension( panelWidth, panelHeight ) );
        maxPanel.setMaximumSize( new Dimension( panelWidth, panelHeight ) );
        maxPanel.setPreferredSize( new Dimension( panelWidth, panelHeight ) );


        add( minPanel );
        add( maxPanel );
        final int numRows = 2;
        final int numCols = 1;
        final int padding = 0;
        SpringUtilities.makeCompactGrid( this, numRows, numCols, padding, padding, padding, padding );
        setBorder( BorderFactory.createTitledBorder( "Date/Time" ) );
    }

    /**
     * Gets the minimum time set in this control as a TimeT object.
     *
     * @return The minimum time.
     */
    public TimeT getMinTime()
    {
        return minDate.getTime();
    }

    /**
     * Gets the maximum time stored in this control as a TimeT object.
     *
     * @return The maximum time.
     */
    public TimeT getMaxTime()
    {
        return maxDate.getTime();
    }

    /**
     * Sets the minimum time stored in this control. Updates the control's
     * display to reflect the new minimum time. No checking is done
     * to ensure that the new minimum time is less than the current maximum time.
     *
     * @param time The time to set.
     */
    public void setMinTime( final TimeT time )
    {
        minDate.setTime( time );
    }


    /**
     * Sets the maximum time stored in this control. Updates the control's
     * display to reflect the new maximum time. No checking is done
     * to ensure that the new maximum time is greater than the current minimum time.
     *
     * @param time The time to set.
     */
    public void setMaxTime( final TimeT time )
    {
        maxDate.setTime( time );
    }


    /**
     * Gets the time range of the control as an Epoch object.
     *
     * @return The Epoch object holding the control's time range.
     */
    public Epoch getTimeRange()
    {
        return new Epoch( minDate.getTime(), maxDate.getTime() );
    }


    /**
     * Sets the time range of the control using the supplied Epoch object.
     * Updates the display to reflect the new time range.
     *
     * @param epoch The supplied time range.
     */
    public void setTimeRange( final Epoch epoch )
    {
        setMinTime( epoch.getTime() );
        setMaxTime( epoch.getEndtime() );
    }

    private final JDateField minDate;
    private final JDateField maxDate;
}
