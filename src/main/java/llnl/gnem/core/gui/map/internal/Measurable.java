package llnl.gnem.core.gui.map.internal;

import llnl.gnem.core.gui.map.ViewPort;

public interface Measurable {
	double distanceFrom( Measurable other);
	boolean isInside(ViewPort viewport);
	boolean intersects(ViewPort viewport);
}
