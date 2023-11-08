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
package llnl.gnem.dftt.core.gui.plotting;

import java.awt.BasicStroke;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */
/**
 * A type-safe enum to represent the dashing style of lines drawn in the axis.
 *
 * @author Doug Dodge
 */
public enum PenStyle {
    NONE(0, "None", null),
    SOLID(1, "Solid", null),
    DOT(2, "Dot", new float[] { 1, 5 }),
    DASH(3, "Dash", new float[] { 10, 10 }),
    DASHDOT(4, "DashDot", new float[] { 10, 5, 1, 5 }),
    DASHDOTDOT(5, "DashDotDot", new float[] { 10, 5, 1, 5, 1, 5 }),
    DASHDASHDOT(6, "DashDashDot", new float[] { 10, 5, 10, 5, 1 }),
    DASHDASHDOTDOT(7, "DashDashDotDot", new float[] { 10, 5, 10, 5, 1, 5, 1 });

    private final int index;
    private final String name;
    private final float[] pattern;

    private PenStyle(int idx, String name, float[] template) {
        index = idx;
        this.name = name;
        pattern = template;
    }

    public int getIndex() {
        return index;
    }

    public static int getMaxIndex() {
        int max = -1;
        for (PenStyle p : getAllStyles()) {
            if (p.getIndex() > max) {
                max = p.getIndex();
            }
        }
        return max;
    }

    /**
     * Return a String description of this type.
     *
     * @return The String description
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets the float array that defines the dashing pattern of a BasicStroke
     * object for this PenStyle.
     *
     * @return The pattern value
     */
    public float[] getPattern() {
        return pattern;
    }

    /**
     * Gets a new stroke of the specified width using the pattern for this
     * PenStyle.
     *
     * @param width Width of the requested BasicStroke
     * @return The new BasicStroke object
     */
    public BasicStroke getStroke(float width) {
        return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, pattern, 0.0f);
    }

    public static PenStyle[] getAllStyles() {
        return PenStyle.values();
    }

    public static PenStyle getPenStyleByIndex(int idx) {
        if (idx < 0 || idx >= PenStyle.values().length) {
            return SOLID;
        }
        return PenStyle.values()[idx];
    }

    public static PenStyle getPenStyle(String style) {
        for (PenStyle astyle : PenStyle.values()) {
            if (astyle.toString().equalsIgnoreCase(style)) {
                return astyle;
            }
        }
        throw new IllegalArgumentException("Invalid style string: " + style);
    }

}
