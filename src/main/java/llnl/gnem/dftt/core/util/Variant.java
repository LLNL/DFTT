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
package llnl.gnem.dftt.core.util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * The Variant class provides a means of passing generic data across an
 * interface. A Variant can be constructed using the primitive types byte, short,
 * int, long, float, double, or with a String. If a Variant instance is constructed
 * using a numeric type, then its value can be accessed as any of the numeric types
 * (subject to truncation of floating point values when retrieved as integral types,
 * and subject to loss of significant bits when casting to a smaller type). All
 * numeric types can be retrieved as a String, but Variants constructed with Strings
 * have no conversion to numeric types regardless of the contents of the string.
 * Attempts to do so will result in a NumberFormatException being thrown.
 *
 * @author Doug Dodge
 */
@SuppressWarnings({"IfStatementWithTooManyBranches"})
public class Variant {
    private Object data;
    private boolean isInteger;
    private boolean isFloatType;
    private boolean isText;
    private boolean isDate;
    private boolean nullData = false;


    /**
     * Copy Constructor for the Variant object
     *
     * @param v the input Variant object to be copied.
     */
    public Variant(Variant v) {
        isInteger = v.isInteger;
        isFloatType = v.isFloatType;
        isText = v.isText;
        isDate = v.isDate;
        if (isInteger)
            data = v.longValue();
        else if (isFloatType)
            data = v.doubleValue();
        else if (isText)
            data = v.toString();
        else
            data = v.dateValue().clone();
    }

    /**
     * Construct a Variant using a byte value.
     *
     * @param v The byte value to store in the Variant
     */
    public Variant(byte v) {
        data = (long) v;
        isInteger = true;
        isFloatType = false;
        isText = false;
        isDate = false;
    }

    /**
     * Construct a Variant using a double value.
     *
     * @param v The double value to store in the Variant
     */
    public Variant(double v) {
        data = v;
        isInteger = false;
        isFloatType = true;
        isText = false;
        isDate = false;
    }

    /**
     * Construct a Variant using a float value.
     *
     * @param v The float value to store in the Variant
     */
    public Variant(float v) {
        data = (double) v;
        isInteger = false;
        isFloatType = true;
        isText = false;
        isDate = false;
    }

    /**
     * Construct a Variant using an int value.
     *
     * @param v The int value to store in the Variant
     */
    public Variant(int v) {
        data = (long) v;
        isInteger = true;
        isFloatType = false;
        isDate = false;
        isText = false;
    }

    /**
     * Construct a Variant using a long value.
     *
     * @param v The long value to store in the Variant
     */
    public Variant(long v) {
        data = v;
        isInteger = true;
        isFloatType = false;
        isText = false;
        isDate = false;
    }

    /**
     * Construct a Variant using a short value.
     *
     * @param v The short value to store in the Variant
     */
    public Variant(short v) {
        data = (long) v;
        isInteger = true;
        isFloatType = false;
        isText = false;
        isDate = false;
    }

    /**
     * Construct a Variant using a String value.
     *
     * @param v The String value to store in the Variant
     */
    public Variant(String v) {
        data = v;
        isInteger = false;
        isFloatType = false;
        isText = true;
        isDate = false;
    }

    /**
     * Construct a Variant using a java.sql.Date
     *
     * @param v The Date value to store in the Variant.
     */
    public Variant(Date v) {
        data = v.clone();
        isInteger = false;
        isFloatType = false;
        isText = false;
        isDate = true;
    }

    /**
     * Check whether the Variant represents a value of type {byte, short, int, long
     * }
     *
     * @return true if the Variant value is of integral type
     */
    public boolean IsInteger() {
        return isInteger;
    }

    /**
     * Check whether the Variant represents a value of type double or float
     *
     * @return return true if the Variant value is of type float or double.
     */
    public boolean IsFloatType() {
        return isFloatType;
    }

    /**
     * Check whethere the Variant represents a String
     *
     * @return return true if the Variant value is a String
     */
    public boolean IsText() {
        return isText;
    }

    /**
     * Check whether the Variant is holding a Date
     *
     * @return Return true if the Variant is a Date.
     */
    public boolean IsDate() {
        return isDate;
    }

    /**
     * Return the Variant value as a byte, possibly losing significance if the size
     * of the value is too large to fit into a byte.
     *
     * @return The byte representation of the Variant
     * @throws NumberFormatException Thrown if the Variant is actually holding a
     *                               String.
     */
    public byte byteValue() throws NumberFormatException {
        if (isInteger) {
            Long s = (Long) data;
            return s.byteValue();
        } else if (isFloatType) {
            Double v = (Double) data;
            return v.byteValue();
        } else
            throw new NumberFormatException("Value is not numeric.");
    }

    /**
     * Return the Variant value as a double, possibly losing significance if the size
     * of the value is too large to fit into a double.
     *
     * @return The double representation of the Variant
     * @throws NumberFormatException Thrown if the Variant is actually holding a
     *                               String.
     */
    public double doubleValue() throws NumberFormatException {
        if (isInteger) {
            Long s = (Long) data;
            return s.doubleValue();
        } else if (isFloatType) return (Double) data;
        else
            throw new NumberFormatException("Value is not numeric.");
    }

    /**
     * Return the Variant value as a float, possibly losing significance if the size
     * of the value is too large to fit into a float.
     *
     * @return The float representation of the Variant
     * @throws NumberFormatException Thrown if the Variant is actually holding a
     *                               String.
     */
    public float floatValue() throws NumberFormatException {
        if (isInteger) {
            Long s = (Long) data;
            return s.floatValue();
        } else if (isFloatType) {
            Double v = (Double) data;
            return v.floatValue();
        } else
            throw new NumberFormatException("Value is not numeric.");
    }

    /**
     * Return the Variant value as a int, possibly losing significance if the size
     * of the value is too large to fit into a int.
     *
     * @return The int representation of the Variant
     * @throws NumberFormatException Thrown if the Variant is actually holding a
     *                               String.
     */
    public int intValue() throws NumberFormatException {
        if (isInteger) {
            Long s = (Long) data;
            return s.intValue();
        } else if (isFloatType) {
            Double v = (Double) data;
            return v.intValue();
        } else
            throw new NumberFormatException("Value is not numeric.");
    }

    /**
     * Return the Variant value as a long, possibly losing significance if the size
     * of the value is too large to fit into a long.
     *
     * @return The long representation of the Variant
     * @throws NumberFormatException Thrown if the Variant is actually holding a
     *                               String.
     */
    public long longValue() throws NumberFormatException {
        if (isInteger) return (Long) data;
        else if (isFloatType) {
            Double v = (Double) data;
            return v.longValue();
        } else
            throw new NumberFormatException("Value is not numeric.");
    }

    /**
     * Return the Variant value as a short, possibly losing significance if the size
     * of the value is too large to fit into a short.
     *
     * @return The short representation of the Variant
     * @throws NumberFormatException Thrown if the Variant is not holding a numeric
     *                               type.
     */
    public short shortValue() throws NumberFormatException {
        if (isInteger) {
            Long s = (Long) data;
            return s.shortValue();
        } else if (isFloatType) {
            Double v = (Double) data;
            return v.shortValue();
        } else
            throw new NumberFormatException("Value is not numeric.");
    }

    /**
     * Return the Variant as a java.sql.Date value.
     *
     * @return The Date representation of the Variant.
     * @throws NumberFormatException Thrown if the Variant is not holding a Date
     *                               type.
     */
    public Date dateValue() throws NumberFormatException {
        if (isDate) {
            Date d = (Date) data;
            return (Date) d.clone();
        } else
            throw new NumberFormatException("Value is not a Date.");
    }

    /**
     * Return the Variant value as a String.
     *
     * @return The String representation of the Variant contents.
     */
    public String toString() {
        if( this.isNullData() )
            return "null";
        if (isInteger) {
            Long s = (Long) data;
            return s.toString();
        } else if (isFloatType) {
            Double v = (Double) data;
            return v.toString();
        } else if (isDate) {
            Date v = (Date) data;
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            return format.format(v).toUpperCase();
        } else
            return (String) data;
    }

    /**
     * Return true if two Variants have the same contents.
     *
     * @param o The Variant to be compared to this Variant.
     * @return true if the contents are the same.
     */
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof Variant) {
            // No need to check for null because instanceof handles that check
            Variant tmp = (Variant) o;
            if (tmp.isText && isText && (data).equals(tmp.data))
                return true;
            else if (tmp.isFloatType && isFloatType && (data).equals(tmp.data))
                return true;
            else if (tmp.isInteger && isInteger && (data).equals(tmp.data))
                return true;
            else return tmp.isDate && isDate && (data).equals(tmp.data);
        } else
            return false;
    }

    /**
     * Return an int hash code based on the contents of the Variant. If the Variant
     * contains a String then use the String::hashCode. If the Variant contains a Floating
     * point value use the Double::hashCode. Otherwise use the Long::hashCode.
     *
     * @return An int hash code value.
     */
    public int hashCode() {
        return (data).hashCode();
    }

    public boolean isNullData() {
        return nullData;
    }

    public void setNullData(boolean nullData) {
        this.nullData = nullData;
    }
}

