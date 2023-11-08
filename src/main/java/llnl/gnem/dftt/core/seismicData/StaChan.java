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

import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author addair1
 */
public class StaChan implements Comparable<StaChan> {
    private final StreamKey key;
    private final Station station;

    public StaChan(Station station, String chan) {
        this.key = new StreamKey(station.getSta(), chan);
        this.station = station;
    }

    public String getChan() {
        return key.getChan();
    }

    public Station getStation() {
        return station;
    }
    
    public String getSta() {
        return station.getSta();
    }
    
    public String getId() {
        return identifier(getSta(), key.getChan());
    }
    
    public StreamKey getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "StaChan{" + "station=" + station.getSta() + ", chan=" + key.getChan() + '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StaChan other = (StaChan) obj;
        return this.key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public int compareTo(StaChan t) {
        return this.key.compareTo(t.key);
    }
    
    public static String identifier(String sta, String chan) {
        return sta + chan;
    }
}
