/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2023 Lawrence Livermore National Laboratory (LLNL)
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
package llnl.gnem.dftt.core.gui.plotting;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JFileChooser;
import javax.swing.RepaintManager;
import javax.swing.filechooser.FileFilter;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.print.PrintTranscoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import llnl.gnem.dftt.core.gui.util.ExceptionDialog;

public class PlotPrinter {

    private final Map<String, PrintService> printerServiceMap;

    private PlotPrinter() {
        printerServiceMap = new HashMap<>();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService printer : printServices) {
            printerServiceMap.put(printer.getName(), printer);
        }

    }

    public void listPrinters() {
        for (String name : printerServiceMap.keySet()) {
            System.out.println("Printer: " + name);
        }
    }

    public PrintService getServiceByName(String name) {
        return printerServiceMap.get(name);
    }

    public PrintService getDefaultService() {
        return PrintServiceLookup.lookupDefaultPrintService();
    }

    public void printCurrentPlot(final JPlotContainer component, boolean printImmediate) {
        PrintService service = getDefaultService();
        if (service == null) {
            throw new IllegalStateException("No printers installed!");
        }
        printCurrentPlot(component, printImmediate, service);
    }

    public void printCurrentPlot(final JPlotContainer component, boolean printImmediate, PrintService service) {
        if (service == null) {
            throw new IllegalStateException("Supplied Printservice is null!");
        }
        try {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setPrintService(service);
            pj.setPrintable((pg, pf, pageNum) -> {
                if (pageNum > 0) {
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2 = (Graphics2D) pg;
                g2.translate(pf.getImageableX(), pf.getImageableY());
                double sx = pf.getImageableWidth() / component.getWidth();
                double sy = pf.getImageableHeight() / component.getHeight();
                g2.scale(sx, sy);
                component.paint(g2);

                return Printable.PAGE_EXISTS;
            });

            if (printImmediate || pj.printDialog()) {
                pj.print();
            }
        } catch (PrinterException ex) {
            Logger.getLogger(JPlotContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BufferedImage createBufferedImage(int dpi, final JPlotContainer gui, int containerWidth,
            int containerHeight) {

        double scale = DrawingUnits.getScale(dpi) * 1.2;
        double WidthInMM = DrawingUnits.getPixelsToMM(containerWidth);
        double heightInMM = DrawingUnits.getPixelsToMM(containerHeight);
        DrawingUnits.setDPI(dpi);

        gui.scaleAllFonts(scale);

        BufferedImage bi = new BufferedImage((int) (containerWidth * scale), (int) (containerHeight * scale),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();

        boolean useClippingRegion = gui.isUseClippingRegion();
        gui.setUseClippingRegion(false);
        boolean drawBox = gui.getPlotBorder().getDrawBox();
        gui.getPlotBorder().setDrawBox(false);
        int fontsize = gui.getTitle().getFontSize();
        Graphics2D g2d = (Graphics2D) g;
        RepaintManager cm = RepaintManager.currentManager(gui);
        cm.setDoubleBufferingEnabled(false);
        gui.setPolyLineUsage(false);
        gui.setForceFullRender(true);

        int offsetToPlotBoxMM = 20;
        gui.Render(g2d, offsetToPlotBoxMM, offsetToPlotBoxMM, WidthInMM, heightInMM);
        gui.getPlotBorder().setDrawBox(drawBox);
        gui.getTitle().setFontSize(fontsize);
        gui.setPolyLineUsage(true);
        gui.setForceFullRender(false);
        cm.setDoubleBufferingEnabled(true);
        DrawingUnits.setToDefault();
        gui.setUseClippingRegion(useClippingRegion);
        gui.scaleAllFonts(1.0 / scale);
        return bi;
    }

    public void exportSVG(JPlotContainer plot) {
        FileFilter svgFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".svg");
            }

            @Override
            public String getDescription() {
                return "SVG Files";
            }
        };

        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(svgFilter);
        chooser.setFileFilter(svgFilter);
        File saveFile = new File("plot.svg");
        chooser.setSelectedFile(saveFile);
        int rval = chooser.showSaveDialog(null);
        if (rval == JFileChooser.APPROVE_OPTION) {
            saveFile = chooser.getSelectedFile();
            try {
                exportSVG(plot, saveFile);
            } catch (UnsupportedEncodingException | FileNotFoundException | SVGGraphics2DIOException e) {
                ExceptionDialog.displayError(e);
            }
        }
    }

    public void exportPlot(JPlotContainer plot, int width, int height, int dpi, String format, String fileName)
            throws IOException {
        if (format.equalsIgnoreCase("SVG")) {
            exportSVG(plot, new File(fileName));
        } else {
            BufferedImage bi = createBufferedImage(dpi, plot, width, height);
            ImageIO.write(bi, format, new File(fileName));
        }
    }

    /**
     * This method will set the settings that lead to best looking svg output of
     * complex shapes.
     */
    private void setupSvgExportSettings(JPlotContainer plot) {
        RepaintManager cm = RepaintManager.currentManager(plot);
        cm.setDoubleBufferingEnabled(false);
        plot.setPolyLineUsage(false);
        plot.setForceFullRender(true);
    }

    /**
     * This method will change back the settings to optimal screen quality (vs.
     * optimal print/svg quality)
     */
    private void revertSvgExportSettings(JPlotContainer plot) {
        RepaintManager cm = RepaintManager.currentManager(plot);
        cm.setDoubleBufferingEnabled(true);
        plot.setPolyLineUsage(true);
        plot.setForceFullRender(false);
    }

    public void exportSVG(JPlotContainer plot, String filename)
            throws UnsupportedEncodingException, FileNotFoundException, SVGGraphics2DIOException {
        exportSVG(plot, new File(filename));
    }

    public void exportSVG(JPlotContainer plot, File file)
            throws UnsupportedEncodingException, FileNotFoundException, SVGGraphics2DIOException {
        // Set the properties that will make for the best quality SVG
        // Copied from: JPlotContainer.printCurrentPlot method
        // Only putting method call here and not in other exportSvg calls because all
        // roads lead here in the end
        // Logic to set these properties is now centralized in JPlotContainer so all
        // apps immediatly see advtange
        setupSvgExportSettings(plot);

        SVGGraphics2D svgGenerator = renderSVG(plot, createDocument());

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        OutputStream stream = new FileOutputStream(file);
        Writer out = new OutputStreamWriter(stream, "UTF-8");
        svgGenerator.stream(out, useCSS);

        // put the settings back to optimal screen quality
        revertSvgExportSettings(plot);
    }

    private void transcode(JPlotContainer plot, PrintTranscoder t, PageFormat format) throws Exception {
        SVGDocument document = createDocument();
        SVGGraphics2D g = new SVGGraphics2D(document);

        double scaleX = format.getImageableWidth() / plot.getWidth();
        double scaleY = format.getImageableHeight() / plot.getHeight();
        double scale = Math.min(scaleX, scaleY);
        g.scale(scale, scale);
        renderSVG(plot, g);

        // Populate the document root with the generated SVG content
        Element root = document.getDocumentElement();
        g.getRoot(root);

        TranscoderInput input = new TranscoderInput(document);
        t.transcode(input, null);
    }

    private SVGGraphics2D renderSVG(JPlotContainer plot, SVGDocument document) {
        // Create an instance of the SVG Generator.
        return renderSVG(plot, new SVGGraphics2D(document));
    }

    private SVGGraphics2D renderSVG(JPlotContainer plot, SVGGraphics2D g) {
        // Ask the test to render into the SVG Graphics2D implementation.
        plot.Render(g);
        return g;
    }

    private static SVGDocument createDocument() {
        // Get a DOMImplementation.
        DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        SVGDocument document = (SVGDocument) domImpl.createDocument(svgNS, "svg", null);
        return document;
    }

    public void printAllPlots(Collection<? extends JPlotContainer> plots) {

        int dpi = 600;
        float plotBoxWidthMM = 150;
        float offsetToPlotBoxMM = 30;
        final ArrayList<BufferedImage> images = new ArrayList<>();
        for (JPlotContainer plot : plots) {
            System.out.println("reimplement printAllPlots!");
            // images.add(JPlotContainer.createBufferedImage(dpi, plot, offsetToPlotBoxMM,
            // plotBoxWidthMM));
        }

        PrinterJob pjob = PrinterJob.getPrinterJob();
        pjob.setPrintable(new ImagePrintable(pjob, new PageFormat(), images));
        pjob.setJobName("Print Current plot");
        pjob.setCopies(1);

        if (pjob.printDialog()) {
            new PrintWorker(pjob).execute();
        }

    }

    public static PlotPrinter getInstance() {
        return PlotPrinterHolder.INSTANCE;
    }

    private static class PlotPrinterHolder {

        private static final PlotPrinter INSTANCE = new PlotPrinter();
    }
}
