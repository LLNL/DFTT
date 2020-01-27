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
package llnl.gnem.core.gui.util;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2005 Lawrence Livermore National Laboratory.
 * User: dodge1
 * Date: Mar 10, 2006
 */
@SuppressWarnings({"MagicNumber"})
public class StatusBarPanel extends JPanel {
    private JComponent leftComponent;
    private JComponent centerComponent;
    private JComponent rightComponent;


    public StatusBarPanel( int leftWidth, int rightWidth )
    {
        super( new BorderLayout() );

        setLeftComponent(new JLabel());
        JPanel tmp = new JPanel( new BorderLayout() );
        tmp.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        tmp.setPreferredSize( new Dimension( leftWidth, 20 ) );
        add( tmp, BorderLayout.WEST );
        tmp.add( leftComponent, BorderLayout.WEST );
        setTextComponents(rightWidth);
    }


    public StatusBarPanel( JProgressBar progressBar, int leftWidth, int rightWidth )
    {
        super( new BorderLayout() );

        setLeftComponent(progressBar);
        JPanel tmp = new JPanel( new BorderLayout() );
        tmp.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        tmp.setPreferredSize( new Dimension( leftWidth, 20 ) );
        add( tmp, BorderLayout.WEST );
        tmp.add( leftComponent, BorderLayout.WEST );


        setTextComponents(rightWidth);


    }


    private void setTextComponents(int rightWidth)
    {
        JPanel tmp;
        setCenterComponent(new JLabel());
        tmp = new JPanel( new BorderLayout() );
        tmp.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        add( tmp, BorderLayout.CENTER );
        tmp.add( centerComponent, BorderLayout.WEST );


        setRightComponent(new JLabel());
        tmp = new JPanel( new BorderLayout() );
        tmp.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        tmp.setPreferredSize( new Dimension( rightWidth, 20 ) );
        add( tmp, BorderLayout.EAST );
        tmp.add( rightComponent, BorderLayout.WEST );
    }


    public void clear()
    {
        SwingUtilities.invokeLater( new barClearAction() );
    }

    class barClearAction implements Runnable{

        public void run()
        {
            if( leftComponent instanceof JLabel )
                ((JLabel)leftComponent).setText("");
            else if( leftComponent instanceof JProgressBar ){
                JProgressBar bar = (JProgressBar) leftComponent;
                bar.setValue(0);
                bar.setMaximum(100);
                bar.setStringPainted(true);
                bar.setIndeterminate(true);
                bar.setVisible(false);
            }
            if( centerComponent instanceof JLabel )
                ((JLabel)centerComponent).setText("");
            if( rightComponent instanceof JLabel )
                ((JLabel)rightComponent).setText("");
        }
    }

    public void setLeftText( String text )
    {
        if( leftComponent instanceof JLabel )
        SwingUtilities.invokeLater( new TextSetter( (JLabel) leftComponent, text ) );
    }

    public void setLeftProgress( ProgressBarMessage msg )
    {
        if( leftComponent instanceof JProgressBar ) {
            JProgressBar progressbar = (JProgressBar) leftComponent;
            setProgressBarVisibility( msg.getShow(), progressbar );
            setProgressBarIndeterminate( msg.getIndeterminate(), progressbar );
            UpdateProgressBar( msg.getCurrent(), msg.getMax(), progressbar );
        }
    }


    public void setCenterText( String text )
    {
        if( centerComponent instanceof JLabel )
        SwingUtilities.invokeLater( new TextSetter( (JLabel)centerComponent, text ) );
    }


    public void setRightText( String text )
    {
        if( rightComponent instanceof JLabel )
        SwingUtilities.invokeLater( new TextSetter( (JLabel) rightComponent, text ) );
    }

    public void setLeftComponent(JComponent leftComponent)
    {
        this.leftComponent = leftComponent;
    }

    public void setCenterComponent(JComponent centerComponent)
    {
        this.centerComponent = centerComponent;
    }

    public void setRightComponent(JComponent rightComponent)
    {
        this.rightComponent = rightComponent;
    }

    class TextSetter implements Runnable {
        private JLabel label;
        private String text;

        public TextSetter( JLabel label, String text )
        {
            this.label = label;
            this.text = text;
        }

        public void run()
        {
            label.setText( text );
        }
    }


    /**
     * Sets the visibility of the status bar. This method should not be be called from the gui
     * event thread. It should only be called from a separate thread in which the computations
     * being monitored by the status bar are occurring.
     *
     * @param visible true or false
     * @param progressbar The progress bar to be manipulated.
     */
    private void setProgressBarVisibility( boolean visible, JProgressBar progressbar )
    {
        SwingUtilities.invokeLater( new ProgressBarVisibility( progressbar, visible ) );
    }

    /**
     * Updates the progress bar. This method must be called from the thread in which the
     * monitored computations are occurring. It must not be called from the gui event thread.
     *
     * @param current The current value of the progress
     * @param max     The maximum attainable value for the progress
     * @param progressbar  The progress bar to be manipulated.
     */
    private void UpdateProgressBar( int current, int max, JProgressBar progressbar )
    {
        SwingUtilities.invokeLater( new ProgressBarUpdater( current, max, progressbar ) );
    }

    /**
     * Sets the progress bar indeterminate state. When the indeterminate state is true,
     * the progress bar will animate but will not display progress. This mode is useful
     * when it is difficult to measure the status of an operation.
     *
     * @param v The indeterminate state
     * @param progressbar  The progress bar to be manipulated.
     */
    private void setProgressBarIndeterminate( boolean v, JProgressBar progressbar )
    {
        SwingUtilities.invokeLater( new ProgressBarStateChanger( v, progressbar ) );
    }




    class ProgressBarVisibility implements Runnable {
        JProgressBar bar;
        boolean visible;

        ProgressBarVisibility( JProgressBar bar, boolean visible )
        {
            this.bar = bar;
            this.visible = visible;
        }

        public void run()
        {
            bar.setVisible( visible );
        }
    }



    static class ProgressBarUpdater implements Runnable {
        int current;
        int max;
        JProgressBar bar;

        ProgressBarUpdater( int current, int max, JProgressBar bar )
        {
            this.current = current;
            this.max = max;
            this.bar = bar;
        }

        public void run()
        {
            bar.setStringPainted(true);
            bar.setMaximum( max );
            bar.setValue( current );
        }
    }

    static class ProgressBarStateChanger implements Runnable {
        boolean indeterminate;
        JProgressBar bar;

        ProgressBarStateChanger( boolean indeterminate, JProgressBar bar )
        {
            this.indeterminate = indeterminate;
            this.bar = bar;
        }

        public void run()
        {
            bar.setIndeterminate( indeterminate );
        }
    }




}
