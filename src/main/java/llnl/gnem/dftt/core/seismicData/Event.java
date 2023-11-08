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
package llnl.gnem.dftt.core.seismicData;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import llnl.gnem.dftt.core.geom.CartesianCoordinate;
import llnl.gnem.dftt.core.geom.Coordinate;
import llnl.gnem.dftt.core.geom.GeographicCoordinate;
import llnl.gnem.dftt.core.geom.Location;
import llnl.gnem.dftt.core.util.TimeT;
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
