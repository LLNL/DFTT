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
package llnl.gnem.core.seismicData;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.geom.GeographicCoordinate;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author addair1
 */
public class EventRecord {
    private final Event<GeographicCoordinate> event;
    private final Origin prefor;
    private final Collection<Origin> origins;

    public EventRecord(double lat, double lon, double depth, double time) {
        this(-1, lat, lon, depth, time);
    }

    public EventRecord(int evid, double lat, double lon, double depth, double time) {
        event = Event.fromGeo(lat, lon, depth, new TimeT(time), evid);
        prefor = new Origin(lat, lon, depth, time);
        origins = new ArrayList<Origin>();
        origins.add(prefor);
    }
    
    public long getEvid() {
        return event.getId();
    }
    
    public double getLat() {
        return event.getCoordinate().getLat();
    }
    
    public double getLon() {
        return event.getCoordinate().getLon();
    }

    public double getDepth() {
        return event.getDepth();
    }

    public double getTime() {
        return event.getTime().getEpochTime();
    }

    public Origin getPreferredOrigin() {
        return prefor;
    }
}
