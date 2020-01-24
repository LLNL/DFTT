package llnl.gnem.core.gui.map.location;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.AnnotationAttributes;
import llnl.gnem.core.gui.map.MapAnnotation;


public abstract class LocationMapAnnotation<InfoType extends LocationInfo> extends MapAnnotation {

	private InfoType info;

	public LocationMapAnnotation(InfoType info, String name, AnnotationAttributes attrs) {
		super(name, getPosition(info), attrs);
		this.info = info;
	}

	public static Position getPosition(LocationInfo info) {
		return new Position(Angle.fromDegrees(info.getLat()), Angle.fromDegrees(info.getLon()), 0);
	}

	/**
	 * Return the internal LocationInfo object
	 * @return
	 */
	public InfoType getInfo() {
		return info;
	}

	@Override
	public String getAnnotation() {
		return info.getMapAnnotation();
	}

}
