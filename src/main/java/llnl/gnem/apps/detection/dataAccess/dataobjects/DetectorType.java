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
package llnl.gnem.apps.detection.dataAccess.dataobjects;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Author: Dave Harris Lawrence Livermore National Laboratory Created: Feb 28,
 * 2008 Time: 3:40:48 PM Last Modified: Feb 28, 2008
 */
public enum DetectorType {

    SUBSPACE(               8, "Subspace",             "SUBSPACE", false ),
    ARRAY_CORRELATION(      7, "ArrayCorrelation",     "ARR_CORR", false ),
    ARRAYPOWER(             4, "ArrayPower",           "ARR_POW",  true  ),
    FSTATISTIC(             3, "Fstatistic",           "FSTAT",    false ),
    BULLETIN(               2, "Bulletin",             "BULLETIN", true  ),
    STALTA(                 1, "STALTA",               "STALTA",   true  );

    /**
     * Automatically generated field: ranks
     */
    private static final int[] ranks = new int[]{ 8, 7, 4, 3, 2, 1 };
    private final String       shortName;
    private final String       name;
    private final int          priority;
    private final boolean      spawning;


    private DetectorType( int priority, String name, String shortName, boolean spawning ) {
        this.priority  = priority;
        this.name      = name;
        this.shortName = shortName;
        this.spawning  = spawning;
    }

    public static int[] getRanks() {
        return ranks;
    }

    public static DetectorType getByPriority(int priority) {
        for (DetectorType type : DetectorType.values()) {
            if (type.getPriority() == priority) {
                return type;
            }
        }
        throw new IllegalArgumentException("Illegal priority value: " + priority);
    }

    public int getPriority() {
        return priority;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @return the spawning
     */
    public boolean isSpawning() {
        return spawning;
    }

    public static Collection<DetectorType> getSpawningDetectorTypes() {
        ArrayList<DetectorType> result = new ArrayList<>();
        for (DetectorType type : DetectorType.values()) {
            if (type.isSpawning()) {
                result.add(type);
            }
        }
        return result;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
