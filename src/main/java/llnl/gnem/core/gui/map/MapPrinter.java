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
package llnl.gnem.core.gui.map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import llnl.gnem.core.gui.map.internal.GifFilter;
import llnl.gnem.core.gui.map.internal.JpgFilter;
import llnl.gnem.core.gui.map.internal.PngFilter;

public class MapPrinter {

    public static void saveImage(final PrintableMap map) {
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new GifFilter());
        chooser.addChoosableFileFilter(new PngFilter());
        chooser.addChoosableFileFilter(new JpgFilter());
        chooser.setFileFilter(new GifFilter());
        File saveFile = new File("mapImage");
        chooser.setSelectedFile(saveFile);
        int rval = chooser.showSaveDialog(null);
        if (rval == JFileChooser.APPROVE_OPTION) {
            saveFile = chooser.getSelectedFile();
            String extension = "";
            File finalFile = null;
            FileFilter chosenFilter = chooser.getFileFilter();
            if (chosenFilter instanceof GifFilter) {
                extension = "GIF";
                finalFile = GifFilter.verifyFile(saveFile);
            } else if (chosenFilter instanceof PngFilter) {
                extension = "PNG";
                finalFile = PngFilter.verifyFile(saveFile);
            } else if (chosenFilter instanceof JpgFilter) {
                extension = "JPEG";
                finalFile = JpgFilter.verifyFile(saveFile);

            }

            if (finalFile != null) {
                BufferedImage image = map.getPlotImage();
                try {
                    ImageIO.write(image, extension, finalFile);
                } catch (IOException ex) {
                    llnl.gnem.core.gui.util.ExceptionDialog.displayError(ex);
                }

            }
        }
    }

    public static void printMap(PrintableMap map) {
        BufferedImage image = map.getPlotImage();
        PrinterJob job = PrinterJob.getPrinterJob();
        Printable printable = new ImagePrinter(image);
        job.setPrintable(printable);
        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                /*
                 * The job did not successfully complete
                 */
            }
        }


    }

    public static class ImagePrinter implements Printable {

        private BufferedImage image;

        public ImagePrinter(BufferedImage image) {
            this.image = image;
        }

        @Override
        public int print(Graphics g, PageFormat pf, int page) throws
                PrinterException {

            if (page > 0) { /*
                 * We have only one page, and 'page' is zero-based
                 */
                return NO_SUCH_PAGE;
            }

            pf.setOrientation(PageFormat.LANDSCAPE);

            int imageHeight = image.getHeight();
            int imageWidth = image.getWidth();

            int paperWidth = 9;
            double scale = (double) paperWidth / imageWidth * 72.0;


            Graphics2D g2d = (Graphics2D) g;
            g2d.scale(scale, scale);
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            AffineTransform affineTransform = new AffineTransform();
            affineTransform.rotate(Math.toRadians(90));
            double centeringShift = (8 - (imageHeight * scale / 72)) / 2;
            int centeringShiftPixels = (int) (centeringShift * 72 / scale);
            affineTransform.translate(200, -(imageHeight + centeringShiftPixels));
            g2d.drawImage(image, affineTransform, null);

            /*
             * tell the caller that this page is part of the printed document
             */
            return PAGE_EXISTS;
        }
    }

}
