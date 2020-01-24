package llnl.gnem.core.gui.map;

import java.util.ArrayList;

public class MapModel {

	private ArrayList<MapListener> views = new ArrayList<MapListener>();

	public void addView(MapListener view) {
		views.add(view);
	}

	public void viewChanged() {
		for (MapListener view : views) {
			view.viewChanged();
		}
	}

}
