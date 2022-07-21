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
package llnl.gnem.core.dataAccess.dataObjects;

import java.util.Objects;
import llnl.gnem.core.metadata.TypeInfo;

/**
 *
 * @author dodge1
 */
public class BandInfo implements TypeInfo{
    private final String band;
    private final String description;
    private final Double minSampleRate;
    private final Double maxSampleRate;

    public BandInfo(String band, String description, Double minSampleRate, Double maxSampleRate) {
        this.band = band;
        this.description = description;
        this.minSampleRate = minSampleRate;
        this.maxSampleRate = maxSampleRate;
    }

    public String getBand() {
        return band;
    }

    @Override
    public String getDescription() {
        return String.format("%s (%f Hz to %f Hz)",description,minSampleRate,maxSampleRate);
    }

    public Double getMinSampleRate() {
        return minSampleRate;
    }

    public Double getMaxSampleRate() {
        return maxSampleRate;
    }

    @Override
    public String toString() {
        return "BandInfo{" + "band=" + band + ", description=" + description + ", minSampleRate=" + minSampleRate + ", maxSampleRate=" + maxSampleRate + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.band);
        hash = 17 * hash + Objects.hashCode(this.description);
        hash = 17 * hash + Objects.hashCode(this.minSampleRate);
        hash = 17 * hash + Objects.hashCode(this.maxSampleRate);
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
        final BandInfo other = (BandInfo) obj;
        if (!Objects.equals(this.band, other.band)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.minSampleRate, other.minSampleRate)) {
            return false;
        }
        if (!Objects.equals(this.maxSampleRate, other.maxSampleRate)) {
            return false;
        }
        return true;
    }

    @Override
    public String getCode() {
        return band;
    }
    
}
