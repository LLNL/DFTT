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
package llnl.gnem.core.correlation.util;

import com.google.common.base.Objects;
import java.io.Serializable;
import llnl.gnem.core.util.TimeT;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */
@ThreadSafe
public class NominalArrival implements Serializable, Comparable<NominalArrival> {

    private static final long serialVersionUID = 584915319740293751L;

    private final String phase;
    private final double time;
    private final String auth;
    private final Long arid;

    @Override
    public String toString() {
        String tmp = String.format("%s at time %s", phase, new TimeT(time).toString());
        if (auth != null) {
            tmp += String.format(" by %s", auth);
        }
        return tmp;
    }

    public NominalArrival(NominalArrival other) {
        phase = other.phase;
        time = other.time;
        auth = other.auth;
        arid = other.arid;
    }

    public NominalArrival(String phase, double time, String auth) {
        this.phase = phase;
        this.time = time;
        this.auth = auth;
        arid = null;
    }

    public NominalArrival(String phase, double time, String auth, long arid) {
        this.phase = phase;
        this.time = time;
        this.auth = auth;
        this.arid = arid;
    }

    public NominalArrival(String phase, double time) {
        this.phase = phase;
        this.time = time;
        this.auth = null;
        arid = null;
    }

    public String getPhase() {
        return phase;
    }

    public double getTime() {
        return time;
    }

    public String getAuth() {
        return auth;
    }

    public Long getArid() {
        return arid;
    }

    @Override
    public int compareTo(NominalArrival o) {
        if (eq(time, o.time) != 0) {
            return eq(time, o.time);
        }
        if (phase != null && phase.compareTo(o.phase) != 0) {
            return phase.compareTo(o.phase);
        }
        if (auth != null && auth.compareTo(o.auth) != 0) {
            return auth.compareTo(o.auth);
        }
        if (arid != null && o.arid != null) {
            return arid.compareTo(o.arid);
        }

        return 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(arid, auth, phase, time);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof NominalArrival) {
            NominalArrival other = (NominalArrival) o;
            if (compareTo(other) == 0) {
                return true;
            }
        }
        return false;
    }

    private static final double EPSILON = 0.00001;

    public static int eq(double d, double d2) {
        if (Math.abs(d - d2) < EPSILON) {
            return 0;
        }
        if (d < d2) {
            return -1;
        }
        return 1;
    }

}
