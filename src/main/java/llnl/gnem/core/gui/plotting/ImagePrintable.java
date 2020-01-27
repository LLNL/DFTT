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
