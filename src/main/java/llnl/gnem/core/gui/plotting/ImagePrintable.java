/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.plotting;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author dodge1
 */
public class ImagePrintable implements Printable {

    private final double x, y, pageWidth;
    private final ArrayList<BufferedImage> images;
    private final double pageHeight;

    public ImagePrintable(PrinterJob printJob, PageFormat pageFormat, Collection<BufferedImage> images) {
        x = pageFormat.getImageableX();
        y = pageFormat.getImageableY();
        pageWidth = pageFormat.getImageableWidth();
        pageHeight = pageFormat.getImageableHeight();
        this.images = new ArrayList<>(images);
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex)
            throws PrinterException {

        Graphics2D g2 = (Graphics2D) g;
        if (pageIndex < images.size()) {
            BufferedImage image = images.get(pageIndex);
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            double scaleW = 1;
            double scaleH = 1;
            if (pageWidth < image.getWidth()) {
                scaleW = pageWidth / image.getWidth();
            }
            if (pageHeight < image.getHeight()) {
                scaleH = pageHeight / image.getHeight();
            }
            double scale = Math.min(scaleW, scaleH);
            if( scale < 1 ){
                imageWidth *= scale;
                imageHeight *= scale;
            }
            
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(image, (int) x, (int) y, imageWidth, imageHeight, null);
            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }

}
