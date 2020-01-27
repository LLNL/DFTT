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
package llnl.gnem.core.gui.plotting.contour;

/**
 *
 * @author dyer1
 */
/**
 * =========================================================================
 * Class MachingSquareIdx sotes an x index and a y index.
 */
public class MachingSquareIdx {
    private int XIndex;
    private int YIndex;

   /**
    * =========================================================================
    * Class constructor.
    *
    * @param XIndex - x index into a grid
    * @param YIndex - y index into a grid
    */
    public MachingSquareIdx(int XIndex, int YIndex) {
        this.XIndex = XIndex;
        this.YIndex = YIndex;

    }


   /**
    * =========================================================================
    * Return the x index into a grid
    *
    * @return XIndex - the x index into a grid
    */
    public int getXIndex() {
        return XIndex;
    }


   /**
    * =========================================================================
    * Return the y index into a grid
    *
    * @return YIndex - the y index into a grid
    */
    public int getYIndex() {
        return YIndex;
    }


   /**
    * =========================================================================
    * Return a String representation of the x and y indices.
    *
    * @return string - String representation of the x and y indices.
    */
    @Override
    public String toString() {
        return String.format("MachingSquareIdx: %d %d", XIndex, YIndex);
    }
}
