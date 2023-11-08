/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess.dataobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import llnl.gnem.apps.detection.core.dataObjects.SlownessSpecification;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class ArrayConfiguration {

    private final String arrayName;
    private final Map<Epoch, Collection<ArrayElementInfo>> epochMap;

    public Collection<String> getArrayStationCodes() {
        Set<String> result = new HashSet<>();
        for (Collection<ArrayElementInfo> cae : epochMap.values()) {
            for (ArrayElementInfo aei : cae) {
                result.add(aei.getStationCode());
            }
        }
        return result;
    }

    public ArrayConfiguration(Collection<ArrayElementInfo> elements) {
        if (elements.isEmpty()) {
            throw new IllegalStateException("Cannot construct array with empty elements collection!");
        }
        String tmp = null;
        for (ArrayElementInfo aei : elements) {
            String name = aei.getArrayName();
            if (tmp == null) {
                tmp = name;
            } else if (!tmp.equals(name)) {
                throw new IllegalStateException("Not all elements belong to the same array!");
            }
        }
        arrayName = tmp;

        epochMap = new HashMap<>();
        for (ArrayElementInfo aei : elements) {
            if (aei.isReferenceElement()) {
                Epoch anEpoch = aei.getEpoch();
                Collection<ArrayElementInfo> myElements = epochMap.get(anEpoch);
                if (myElements == null) {
                    myElements = new ArrayList<>();
                    epochMap.put(anEpoch, myElements);
                }
                myElements.add(aei);
            }
        }

        // Now organize the collection of elements which may belong to more than a single reference element epoch
        // so they are organized by the proper reference element epoch.
        for (Epoch e : epochMap.keySet()) {
            Collection<ArrayElementInfo> foo = epochMap.get(e);
            for (ArrayElementInfo aei : elements) {
                if (!aei.isReferenceElement()) {
                    Epoch anEpoch = aei.getEpoch();
                    if (e.ContainsTime(anEpoch.getbeginning())) {
                        foo.add(aei);
                    }
                }
            }
        }

    }

    public String getArrayName() {
        return arrayName;
    }

    public Collection<Epoch> getEpochs() {
        return epochMap.keySet();
    }

    public Collection<ArrayElementInfo> getElements(double time) {
        for (Epoch e : epochMap.keySet()) {
            if (e.ContainsTime(new TimeT(time))) {
                return new ArrayList<>(epochMap.get(e));
            }
        }
        return new ArrayList<>();
    }

    public ArrayElementInfo getElement(StreamKey key, int jdate) {
        TimeT atime = TimeT.jdateToTimeT(jdate);
        for (Epoch e : epochMap.keySet()) {
            if (e.ContainsTime(atime)) {
                Collection<ArrayElementInfo> elements = epochMap.get(e);
                for (ArrayElementInfo aei : elements) {
                    if (aei.getStationCode().equals(key.getSta())) {
                        return aei;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Array{" + "arrayName=" + arrayName + ", epochMap=" + epochMap + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.arrayName);
        hash = 19 * hash + Objects.hashCode(this.epochMap);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ArrayConfiguration other = (ArrayConfiguration) obj;
        if (!Objects.equals(this.arrayName, other.arrayName)) {
            return false;
        }
        if (!Objects.equals(this.epochMap, other.epochMap)) {
            return false;
        }
        return true;
    }

    public boolean hasElement(String sta) {
        for (Collection<ArrayElementInfo> caei : epochMap.values()) {
            for (ArrayElementInfo aei : caei) {
                if (aei.getStationCode().equals(sta)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<StreamKey, ArrayElementInfo> getElements(Collection<StreamKey> channels, int jdate) {
        Map<StreamKey, ArrayElementInfo> result = new HashMap<>();
        TimeT atime = TimeT.getTimeFromJulianDate(jdate);
        for (Epoch e : epochMap.keySet()) {
            if (e.ContainsTime(atime)) {
                Collection<ArrayElementInfo> elements = epochMap.get(e);
                for (StreamKey key : channels) {
                    for (ArrayElementInfo aei : elements) {
                        if (aei.getStationCode().equals(key.getSta())) {
                            result.put(key, aei);
                        }
                    }
                }
            }
        }
        if (result.size() != channels.size()) {
            throw new IllegalStateException("Could not retrieve an array element for all stream keys!");
        }
        return result;
    }

    public double[] delaysInSeconds(Collection<StreamKey> channels, SlownessSpecification slownessSpecification) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
