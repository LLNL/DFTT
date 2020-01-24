package llnl.gnem.core.gui.map;

import llnl.gnem.core.polygon.BasePolygon;

public interface EditableMapView {
	public void completedDrawPolygon(BasePolygon polygon);
	public void completedMovedPolygon(BasePolygon polygon);
}
