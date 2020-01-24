package llnl.gnem.core.gui.map;

import llnl.gnem.core.gui.map.internal.IconManager;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;

public abstract class MapAnnotation extends GlobeAnnotation {

	public MapAnnotation(String name, Position position, AnnotationAttributes attrs) {
		super(name, position, attrs);
	}

	/**
	 * Return the string containing the map object mouse-over annotation
	 * @return
	 */
	public abstract String getAnnotation();

	public abstract void updateIcon(IconManager iconManager, boolean selected);

	public void unhighlight() {
        this.getAttributes().setHighlighted(false);
	}

	public void highlight() {
        this.getAttributes().setHighlighted(true);
	}
}
