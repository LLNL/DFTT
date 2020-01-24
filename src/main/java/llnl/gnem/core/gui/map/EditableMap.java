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
