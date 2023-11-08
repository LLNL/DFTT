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

//import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.GaussianBlurFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import org.jdesktop.swingx.util.GraphicsUtilities;

/**
 * Created by dodge1
 * Date: Apr 16, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class CustomGlassPane extends JComponent {
    private BufferedImage blurBuffer;
    private BufferedImage backBuffer;
    private final float alpha = 0.9f;
    private boolean blurDisplay;


    @SuppressWarnings({"EmptyClass"})
    public CustomGlassPane() {
        blurDisplay = false;
        addMouseListener(new MouseAdapter() {
        });
        addMouseMotionListener(new MouseMotionAdapter() {
        });
        addKeyListener(new KeyAdapter() {
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent evt) {
                requestFocusInWindow();
            }
        });

        setFocusTraversalKeysEnabled(false);
    }


    @Override
    protected void paintComponent(Graphics g) {
        if (blurDisplay) {
            if (isVisible() && blurBuffer != null) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(backBuffer, 0, 0, null);

                g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
                g2.drawImage(blurBuffer, 0, 0, getWidth(), getHeight(), null);
                g2.dispose();
            }
        } else {
            Rectangle clip = g.getClipBounds();
            Color alphaWhite = new Color(0.0f, 0.0f, 0.0f, 0.65f);
            g.setColor(alphaWhite);
            g.fillRect(clip.x, clip.y, clip.width, clip.height);
        }


    }

    private void createBlur() {
        if( getWidth() <= 0 || getHeight() <= 0)
            return;
        
        JRootPane root = SwingUtilities.getRootPane(this);
        blurBuffer = GraphicsUtilities.createCompatibleImage(
                getWidth(), getHeight());
        Graphics2D g2 = blurBuffer.createGraphics();
        root.paint(g2);
        g2.dispose();

        backBuffer = blurBuffer;

        blurBuffer = GraphicsUtilities.createThumbnailFast(
                blurBuffer, getWidth() / 2);
        blurBuffer = new GaussianBlurFilter(5).filter(blurBuffer, null);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible)
            createBlur();
        else {

            backBuffer = null;

            blurBuffer = null;
        }
    }


    public void setBlurDisplay(boolean blurDisplay) {
        this.blurDisplay = blurDisplay;
    }
}
