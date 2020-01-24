package llnl.gnem.core.gui.map.internal;

import llnl.gnem.core.gui.map.MapAnnotation;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.AnnotationAttributes;

public abstract class RenderItemAnnotation extends MapAnnotation implements Measurable
{
	public RenderItemAnnotation(String title, Position position, AnnotationAttributes defaults) {
		super("", position, defaults);
	}
}
