package llnl.gnem.core.gui.plotting.plotobject;

import java.awt.*;
import java.util.logging.Level;
import llnl.gnem.core.util.ApplicationLogger;

/**
 * Class that is used to create Symbol instances defined by the supplied SymbolStyle and other parameters.
 */

public class SymbolFactory {
    private static final String classPackage = SymbolFactory.class.getPackage().getName() + ".";

    /**
     * @param style    The type of symbol to create
     * @param X        The X-center of the symbol in real-world coordinates
     * @param Y        The Y-center of the symbol in real-world coordinates
     * @param size     The size of the Symbol in mm
     * @param fillC    The fill color for the symbol
     * @param edgeC    The edge color for the symbol
     * @param textC    The color of the text associated with the symbol
     * @param text     The text string to be plotted with the symbol
     * @param visible  The visibility of the symbol
     * @param textVis  The visibility of the associated text
     * @param fontsize The font size of the associated text.
     * @return A fully constructed Symbol ready for use.
     */
    public static Symbol createSymbol(SymbolStyle style, double X, double Y, double size, Color fillC, Color edgeC,
                                      Color textC, String text, boolean visible, boolean textVis, double fontsize)
    {
     
        try {
            Symbol s = (Symbol) Class.forName(classPackage + style.toString()).newInstance();
            s.setXcenter(X);
            s.setYcenter(Y);
            s.setSymbolSize(size);
            s.setFillColor(fillC);
            s.setEdgeColor(edgeC);
            s.setTextColor(textC);
            s.setText(text);
            s.setVisible(visible);
            s.setTextVisible(textVis);
            s.setFontSize(fontsize);
            return s;
        }
        catch (Exception e) {
            ApplicationLogger.getInstance().log(Level.SEVERE, "Failed to create symbol!", e);
            return null;
        }
    }


    public static Symbol createSymbol(SymbolDef symboldef) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Symbol s = (Symbol) Class.forName(classPackage + symboldef.getStyle().toString()).newInstance();
        s.setSymbolSize(symboldef.getSize());
        s.setFillColor(symboldef.getFillColor());
        s.setEdgeColor(symboldef.getEdgeColor());
        return s;
    }


}
