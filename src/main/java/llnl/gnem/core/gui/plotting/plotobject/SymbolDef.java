package llnl.gnem.core.gui.plotting.plotobject;

import net.jcip.annotations.ThreadSafe;

import java.awt.*;

/**
 * Created by dodge1
 * Date: Feb 6, 2008
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */


@ThreadSafe
public class SymbolDef {
    private final SymbolStyle style;
    private final double size;
    private final Color fillColor;
    private final Color edgeColor;

    public SymbolDef(SymbolStyle style, double size, Color fillColor, Color edgeColor )
    {
        this.style = style;
        this.size = size;
        this.fillColor = fillColor;
        this.edgeColor = edgeColor;
    }

    public SymbolStyle getStyle()
    {
        return style;
    }

    public double getSize()
    {
        return size;
    }

    public Color getFillColor()
    {
        return fillColor;
    }

    public Color getEdgeColor()
    {
        return edgeColor;
    }
}
