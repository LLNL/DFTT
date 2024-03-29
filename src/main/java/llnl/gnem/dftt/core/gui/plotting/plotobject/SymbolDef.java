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
package llnl.gnem.dftt.core.gui.plotting.plotobject;

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
