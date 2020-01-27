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
package llnl.gnem.core.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.polygon.Polygon;
import llnl.gnem.core.polygon.PolygonSet;
import llnl.gnem.core.polygon.PolygonSetType;

/**
 *
 * @author dodge1
 */
public interface PolygonDAO {

    int insertPolygon(final int polygonSetId, Polygon polygon) throws DataAccessException;

    void updatePolygonSetBounds(PolygonSet set) throws DataAccessException;

    void deletePolygonSet(PolygonSet set) throws DataAccessException;

    Integer polygonExistsInPolygonSet(int polygonSetId, Polygon poly)
            throws DataAccessException;

    PolygonSet retrievePolyset(int polysetid) throws DataAccessException;

    PolygonSet insertPolygonSet(PolygonSetType type, String name, Collection<Polygon> polygons)
            throws DataAccessException;

    public void deletePolygon(int polyid)
            throws DataAccessException;

    public Collection<PolygonSet> getAllPolygonSets() throws DataAccessException;
}
