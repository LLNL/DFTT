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
package llnl.gnem.dftt.core.dataAccess.selectionCriteria.position;

import java.util.Objects;

/**
 *
 * @author dodge1
 */
class LatLonRestrictionImpl implements PositionRestriction {

    private final LatLonBox allowableBox;

    public LatLonRestrictionImpl(LatLonBox box) {
        allowableBox = box;
    }

    public LatLonRestrictionImpl(double minLat, double maxLat, double minLon, double maxLon) {
        allowableBox = new LatLonBox(minLat, maxLat, minLon, maxLon);
    }

    @Override
    public boolean isInside(double lat, double lon) {
        return allowableBox.contains(lat, lon);
    }

    @Override
    public LatLonBox getSqlLimits() {
        return allowableBox;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.allowableBox);
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
        final LatLonRestrictionImpl other = (LatLonRestrictionImpl) obj;
        if (!Objects.equals(this.allowableBox, other.allowableBox)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LatLonRestriction{" + "allowableBox=" + allowableBox + '}';
    }

    @Override
    public String getSQLClause() {
        String sql = String.format(" and lat between %f and %f and lon between %f and %f ",
                allowableBox.getMinLat(), allowableBox.getMaxLat(),
                allowableBox.getMinLon(), allowableBox.getMaxLon());
        return sql;
    }

}
