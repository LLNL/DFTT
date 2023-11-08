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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

/**
 * Created by dodge1 Date: Mar 4, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class ProgressPanel extends JPanel {

    private static final long serialVersionUID = 437514089288982208L;

    private final JLabel titleLabel;
    private final JProgressBar progressBar;
    private final JLabel msgLabel;

    public ProgressPanel() {
        super(new SpringLayout());
        titleLabel = new JLabel("Please wait..");
        progressBar = new JProgressBar();
        progressBar.setBorderPainted(true);
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setMinimumSize(new Dimension(200, 20));
        progressBar.setStringPainted(true);
        msgLabel = new JLabel("Processing");
        msgLabel.setVisible(true);
        msgLabel.setBackground(Color.red);
        progressBar.setVisible(true);

        add(titleLabel);
        add(progressBar);
        add(msgLabel);

  //      setPreferredSize(new Dimension(320, 80));
  //      setMinimumSize(new Dimension(320, 80));
  //      setMaximumSize(new Dimension(320, 80));
        SpringUtilities.makeCompactGrid(this,
                3, 1, //rows, cols
                6, 6, //initX, initY
                10, 6);       //xPad, yPad
    }

    public void setProgressStringPainted(boolean painted) {
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setStringPainted(painted);
        } else {
            StringPaintedSetter setter = new StringPaintedSetter(painted);
            SwingUtilities.invokeLater(setter);
        }
    }

    public void setLabelVisibility(boolean visible) {
        if (SwingUtilities.isEventDispatchThread()) {
            msgLabel.setVisible(visible);
        } else {
            SwingUtilities.invokeLater(new LabelVisibilitySetter(visible));
        }
    }

    public void setMinMax(int minValue, int maxValue) {
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setMinimum(minValue);
            progressBar.setMaximum(maxValue);
        } else {
            SwingUtilities.invokeLater(new MinMaxSetter(minValue, maxValue));
        }
    }

    public void setProgressBarIndeterminate(boolean indeterminate) {
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setIndeterminate(indeterminate);
        } else {
            SwingUtilities.invokeLater(new IndeterminantSetter(indeterminate));
        }
    }

    public void setValue(int value) {
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setValue(value);
        } else {
            ValueSetter valueSetter = new ValueSetter(value);
            SwingUtilities.invokeLater(valueSetter);
        }
    }

    public void setText(String text) {
        if (SwingUtilities.isEventDispatchThread()) {
            msgLabel.setText(text);
        } else {
            TextSetter textSetter = new TextSetter(text);
            SwingUtilities.invokeLater(textSetter);
        }
    }

    public void setProgress(boolean visible, boolean indeterminant, boolean labelVisible, boolean paintProgressString, String text) {
        setProgressBarIndeterminate(indeterminant);
        setLabelVisibility(labelVisible);
        setProgressStringPainted(paintProgressString);
        setText(text);
        setVisible(visible);
    }

    public void initProgress(String title, int width, int height) {
        int width1 = width;
        int height1 = height;

        if ((width1 <= 0) || (height1 <= 0)) {
            height1 = 60;
            width1 = 400;
        }
 //       setSize(new Dimension(width1, height1));
    }

    public void setTitle(final String title) {

        if (SwingUtilities.isEventDispatchThread()) {
            titleLabel.setText(title);
        } else {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    titleLabel.setText(title);
                }
            });


        }

    }

    private class StringPaintedSetter implements Runnable {

        private final boolean painted;

        private StringPaintedSetter(boolean painted) {
            this.painted = painted;
        }

        @Override
        public void run() {
            progressBar.setStringPainted(painted);
        }
    }

    private class LabelVisibilitySetter implements Runnable {

        private final boolean visible;

        private LabelVisibilitySetter(boolean visible) {
            this.visible = visible;
        }

        @Override
        public void run() {
            msgLabel.setVisible(visible);
        }
    }

    private class MinMaxSetter implements Runnable {

        private final int minValue;
        private final int maxValue;

        private MinMaxSetter(int minValue, int maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public void run() {
            progressBar.setMinimum(minValue);
            progressBar.setMaximum(maxValue);
        }
    }

    private class IndeterminantSetter implements Runnable {

        private final boolean indeterminate;

        private IndeterminantSetter(boolean indeterminate) {
            this.indeterminate = indeterminate;
        }

        @Override
        public void run() {
            progressBar.setIndeterminate(indeterminate);
        }
    }

    private class ValueSetter implements Runnable {

        private final int value;

        private ValueSetter(int value) {
            this.value = value;
        }

        @Override
        public void run() {
            progressBar.setValue(value);
        }
    }

    private class TextSetter implements Runnable {

        private final String value;

        private TextSetter(String value) {
            this.value = value;
        }

        @Override
        public void run() {
            msgLabel.setText(value);
        }
    }
}
