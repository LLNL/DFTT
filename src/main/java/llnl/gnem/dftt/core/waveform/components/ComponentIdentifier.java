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
package llnl.gnem.dftt.core.waveform.components;

import net.jcip.annotations.ThreadSafe;

/**
 * Created by dodge1 Date: Mar 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@ThreadSafe
public class ComponentIdentifier {

    private final ComponentType type;
    private final String orientation;
    private final String locid;

    public ComponentIdentifier(String band,
            String instrument,
            String orientation,
            String locid) {
        this.type = new ComponentType(band + instrument);
        this.orientation = orientation;
        this.locid = locid;
        if (type == null) {
            throw new IllegalStateException("Unrecognized component type (" + band + instrument + ")!");
        }
    }

    /**
     *
     * @param chan
     */
    public ComponentIdentifier(String chan) {
        String band = chan.substring(0, 1).toUpperCase();
        String instrument = chan.substring(1, 2).toUpperCase();
        this.type = new ComponentType(band + instrument);
        if (type == null) {
            throw new IllegalStateException("Unrecognized component type (" + band + instrument + ")!");
        }

        orientation = chan.substring(2, 3).toUpperCase();
        if (chan.length() > 3) {
            locid = chan.substring(3, chan.length());
        } else {
            locid = "  ";
        }
    }

    public ComponentIdentifier(ComponentType type, String orientation, String locid) {
        this.type = type;
        this.orientation = orientation;
        this.locid = locid;
    }

    public ComponentIdentifier(ComponentIdentifier other) {
        this.type = other.type;
        this.orientation = other.orientation;
        this.locid = other.locid;
    }

    @Override
    public String toString() {
        return getLabelString();
//        String objectId = Integer.toHexString(System.identityHashCode(this));
//        return String.format("(ComponentIdentifier(%s) %s, Orientation=%s, locid = %s)", objectId, type.toString(), orientation, locid);
    }
    
    public String getLabelString()
    {
        return String.format(" %s%s - (%s)",  type.getId(), orientation, locid);
    }

    public ComponentType getType() {
        return type;
    }

    public String getBand() {
        return type.getBand();
    }

    public String getInstrument() {
        return type.getInstrument();
    }

    public String getOrientation() {
        return orientation;
    }

    public String getLocid() {
        return locid;
    }

    public boolean isCompatibleWith(ComponentIdentifier identifier) {
        return type.isCompatibleWith(identifier.type) && locid.equals(identifier.locid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComponentIdentifier other = (ComponentIdentifier) o;
        return type.equals(other.type) && locid.equals(other.locid) && orientation.equals(other.orientation);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + orientation.hashCode();
        result = 31 * result + locid.hashCode();
        return result;
    }

    public String getChan() {
        return (type.getBand() + type.getInstrument()+orientation+locid).trim();
    }
}
