package llnl.gnem.core.gui.util;

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
