/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.dataAccess.selectionCriteria;

import java.util.Objects;

/**
 *
 * @author dodge1
 */
public class DoubleRangeRestriction {

    /**
     * @return the minValue
     */
    public Double getMinValue() {
        return minValue;
    }

    /**
     * @return the maxValue
     */
    public Double getMaxValue() {
        return maxValue;
    }

    private final Double minValue;
    private final Double maxValue;
    private final String columnName;

    public DoubleRangeRestriction(Double minValue, Double maxValue, String columnName) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.columnName = columnName;
    }

    protected String getSQLClause() {
        if (minValue == null || maxValue == null) {
            return " ";
        } else {
            return String.format(" and %s between %f and %f", columnName, minValue, maxValue);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.minValue);
        hash = 83 * hash + Objects.hashCode(this.maxValue);
        hash = 83 * hash + Objects.hashCode(this.columnName);
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
        final DoubleRangeRestriction other = (DoubleRangeRestriction) obj;
        if (!Objects.equals(this.columnName, other.columnName)) {
            return false;
        }
        if (!Objects.equals(this.minValue, other.minValue)) {
            return false;
        }
        if (!Objects.equals(this.maxValue, other.maxValue)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DoubleRangeRestriction{" + "minValue=" + minValue + ", maxValue=" + maxValue + ", columnName=" + columnName + '}';
    }
    
}
