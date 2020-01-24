package llnl.gnem.core.seismicData;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import llnl.gnem.core.geom.CartesianCoordinate;
import llnl.gnem.core.geom.Coordinate;
import llnl.gnem.core.geom.GeographicCoordinate;
import llnl.gnem.core.geom.Location;
import llnl.gnem.core.util.TimeT;
import net.jcip.annotations.Immutable;

@Immutable
public class Event<T extends Coordinate> extends Location<T> {
	private final long evid;
	private final TimeT time;
	private final Energy energy;

	public Event(T coord, TimeT time) {
		this(coord, time, -1);
	}

	public Event(T coord, Energy energy) {
		this(coord, null, energy, -1);
	}

	public Event(T coord, TimeT time, long evid) {
		this(coord, time, null, evid);
	}

	public Event(T coord, TimeT time, long evid, String name) {
		this(coord, time, null, evid, name);
	}

	public Event(T coord, TimeT time, Energy energy) {
		this(coord, time, energy, -1);
	}

	public Event(T coord, TimeT time, Energy energy, long evid) {
		super(coord, evid + "");
		this.evid = evid;
		this.time = time;
		this.energy = energy;
	}

	@JsonCreator
	public Event(@JsonProperty("coordinate") T coord, @JsonProperty("time") TimeT time, @JsonProperty("energy") Energy energy, @JsonProperty("id") long evid, @JsonProperty("name") String name) {
		super(coord, name);
		this.evid = evid;
		this.time = time;
		this.energy = energy;
	}

	@JsonIgnore
	public double getDepth() {
		return -getCoordinate().getElevation();
	}

	public boolean knownTime() {
		return time != null;
	}

	public TimeT getTime() {
		return time;
	}

	public boolean knownEnergy() {
		return energy != null;
	}

	public Energy getEnergy() {
		return energy;
	}

	public long getId() {
		return evid;
	}

	public static Event<GeographicCoordinate> fromGeo(double lat, double lon) {
		return new Event<>(new GeographicCoordinate(lat, lon), new TimeT());
	}

	public static Event<GeographicCoordinate> fromGeo(double lat, double lon, double depth, TimeT time, long evid) {
		return new Event<>(new GeographicCoordinate(lat, lon, -depth), time, evid);
	}

	public static Event<GeographicCoordinate> fromGeo(double lat, double lon, double depth, TimeT time, long evid, String name) {
		return new Event<>(new GeographicCoordinate(lat, lon, -depth), time, evid, name);
	}

	public static Event<GeographicCoordinate> fromGeo(double lat, double lon, double depth, TimeT time, Energy energy, long evid) {
		return new Event<>(new GeographicCoordinate(lat, lon, -depth), time, energy, evid);
	}

	public static Event<CartesianCoordinate> fromCartesian(double x, double y, double depth, TimeT time, long evid) {
		return new Event<>(new CartesianCoordinate(x, y, -depth), time, evid);
	}

	public static Event<CartesianCoordinate> fromCartesian(double x, double y, double depth, TimeT time, Energy energy, long evid) {
		return new Event<>(new CartesianCoordinate(x, y, -depth), time, energy, evid);
	}
}
