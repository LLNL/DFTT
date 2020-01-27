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
package llnl.gnem.core.polygon;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * User: dodge1
 * Date: Jan 22, 2004
 * Time: 1:43:24 PM
 * To change this template use Options | File Templates.
 */
public class line {
    public Vector3D p1;
    public Vector3D p2;

    public line()
    {
        p1 = new Vector3D( 0.0, 0.0, 0.0 );
        p2 = new Vector3D( 0.0, 0.0, 0.0 );
    }

    public line( Vector3D pp1, Vector3D pp2 )
    {
        p1 = new Vector3D( pp1.getX(), pp1.getY(), pp1.getZ() );
        p2 = new Vector3D( pp2.getX(), pp2.getY(), pp2.getZ() );
    }

}


