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
package llnl.gnem.core.gui.map;

import llnl.gnem.core.gui.map.location.LocationInfo;
import llnl.gnem.core.polygon.PolygonSet;
import llnl.gnem.core.polygon.PolygonSetType;
import llnl.gnem.core.seismicData.Network;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class NetworkInfo extends LocationInfo<Network> {
    private static double mid(double min, double max) {
        return (min + max) * 0.5;
    }

    public NetworkInfo(String code, int id,
            double minLat, double maxLat, double minLon, double maxLon) {
        super(new Network(mid(minLat, maxLat), mid(minLon, maxLon), code,
                new PolygonSet(PolygonSetType.network, code, id, minLat, maxLat, minLon, maxLon)));
    }

    public NetworkInfo(PolygonSet polySet) {
        super(new Network(
                mid(polySet.getMinLat(), polySet.getMaxLat()),
                mid(polySet.getMinLon(), polySet.getMaxLon()),
                polySet.getName(), polySet));
    }

    public PolygonSet getPolySet() {
        return getLocation().getPolygonSet();
    }

    @Override
    public String getMapAnnotation() {
        return String.format("Network=%s MinLat,MinLon=[%5.3f,%5.3f] MaxLat,MaxLon=[%5.3f,%5.3f]",
                this.getName(),
                getPolySet().getMinLat(), getPolySet().getMinLon(),
                getPolySet().getMaxLat(), getPolySet().getMaxLon());
    }
}
