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

import java.util.List;

import llnl.gnem.core.polygon.BasePolygon;

public interface EditableMap {

	/**
	 * Set whether of not the map is in draw polygon mode.
	 * @param editable
	 */
	public void setPolygonEditable(boolean editable);

	/**
	 * Return whether or not the map is currently in draw polygon mode.
	 * @return
	 */
	public boolean getPolygonEditable();

	/**
	 * Clear all of the polygons in the Map BasePolygon Layer
	 */
	public void clearPolygons();

	/**
	 * Add the given polygon to the Map BasePolygon Layer
	 * @param poly
	 */
	public void addPolygon(BasePolygon poly);

	/**
	 * Return the list of polygons currently contained within the Map BasePolygon Layer
	 * @return
	 */
	public List<BasePolygon> getPolygons();

	/**
	 * Add a callback handler to the EditableMap
	 *
	 * @param view
	 */
	public void addEditableMapView(EditableMapView view);
}
