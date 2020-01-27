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
package llnl.gnem.core.gui.plotting.beachball;

import llnl.gnem.core.util.PairT;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

/**
 * Created by dodge1
 * Date: Mar 5, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class BeachBallImage extends JPanel {

    private final int imageSize;
    private final Boundaries bounds;
    private final Color color;

    public BeachBallImage(double strike, double dip, double rake, int size, Color color) {
//        this.setSize(size, size);
        this.imageSize = size;
        this.color = color;
        bounds = BeachballOps.getBoundaries(strike, dip, rake, size / 2, size / 2, size / 2.2);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        render(g);

    }

    public BufferedImage createBufferedImage() {
        BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        render(g2d);
        g2d.dispose();
        return image;
    }

    private void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Shape shape = new Rectangle(0, 0, imageSize, imageSize);
        g2d.setPaint(Color.WHITE);
        g2d.fill(shape);
        g2d.setColor(color);

        GeneralPath path = new GeneralPath();
        double[] x2 = bounds.getX2();
        double[] y2 = bounds.getY2();
        path.moveTo((float) x2[0], imageSize - (float) y2[0]);
        for (int j = 1; j < x2.length; ++j) {
            path.lineTo((float) x2[j], imageSize - (float) y2[j]);

        }
        g2d.fill(path);

        path = new GeneralPath();
        double[] x1 = bounds.getX1();
        double[] y1 = bounds.getY1();
        path.moveTo((float) x1[0], imageSize - (float) y1[0]);
        for (int j = 1; j < x1.length; ++j) {
            path.lineTo((float) x1[j], imageSize - (float) y1[j]);

        }
        g2d.fill(path);

        path = new GeneralPath();
        PairT<double[], double[]> circle = bounds.getBoundingCircle();
        double[] xc = circle.getFirst();
        double[] yc = circle.getSecond();
        path.moveTo((float) xc[0], imageSize - (float) yc[0]);
        for (int j = 1; j < xc.length; ++j) {
            path.lineTo((float) xc[j], imageSize - (float) yc[j]);

        }
        g2d.setColor(color);

        g2d.draw(path);

        double[] xPaxis = bounds.getXPaxis();
        double[] yPaxis = bounds.getYPaxis();
        if (xPaxis != null && xPaxis.length > 0) {
            for (int j = 0; j < xPaxis.length; ++j) {
                g2d.drawString("P", (int) xPaxis[j], imageSize - (int) yPaxis[j]);
            }
        }
    }
}
