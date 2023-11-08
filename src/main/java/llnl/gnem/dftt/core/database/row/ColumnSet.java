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
package llnl.gnem.dftt.core.database.row;

import llnl.gnem.dftt.core.database.column.StringColumn;
import llnl.gnem.dftt.core.database.column.IntColumn;
import llnl.gnem.dftt.core.database.column.Lddate;
import llnl.gnem.dftt.core.database.column.FloatColumn;
import llnl.gnem.dftt.core.database.column.Column;
import llnl.gnem.dftt.core.database.column.CssVersion;
import java.sql.Date;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import llnl.gnem.dftt.core.database.column.DuplicateColumnException;
import llnl.gnem.dftt.core.util.TimeT;
import llnl.gnem.dftt.core.util.Variant;

/*
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.
 */

/**
 * The ColumnSet encapsulates a collection of Column objects. It can serve a number
 * of different purposes. For example, a row retrieved from a database query is
 * returned as a ColumnSet. A Primary or Candidate Key is stored as a ColumnSet.
 * Given a ColumnSet, you can access any of the Columns to get or set its value.
 *
 * @author Doug Dodge
 */
public class ColumnSet {
    private static final String CLASS_PACKAGE = Column.class.getPackage().getName() + ".";
    private static CssVersion version = CssVersion.Llnl;

    /**
     * Constructor for the ColumnSet object
     *
     * @param items This parameter can be either a Vector of
     *              Columns or a Vector of Column names. In the first case, each Column is copied
     *              and added to this ColumnSet. In the second case a collection of unitialized
     *              Columns is created and added to this ColumnSet. It is possible to mix Columns
     *              and Column names in this input Vector. This will result in some Columns
     *              being initialized and some not, depending on the form of their input.
     */
    public ColumnSet(List items) {
        columns = new Vector<Column>();
        columnMap = new Hashtable<>();
        try {
            for (Object item : items) {
                Column col;
                if (item instanceof Column) {
                    // Create a new Column by copying the input Object
                    Column c = (Column) item;
                    col = (Column) c.clone();
                } else if (item instanceof String) {
                    // Create a new Column from a name (Set to Column default)
                    String name = (String) item;

                    Object obj = Class.forName(CLASS_PACKAGE + name).newInstance();
                    col = (Column) obj;
                } else {
                    // The current Object is neither a Column or a String so we cannot proceed.
                    StringBuilder s = new StringBuilder("Attempted to add the Object: " + item.toString());
                    s.append(" which is not a Column or a String specifier for a Column");
                    throw new IllegalArgumentException(s.toString());
                }
                if (hasColumn(col.getName())) {
                    // This Column already exists in thsi ColumnSet. Cannot have two of the same Column.
                    StringBuilder s = new StringBuilder("Attempted to add the Column named: " + col.getName());
                    s.append(" when this Column already exists in this ColumnSet.");
                    throw new DuplicateColumnException(s.toString());
                }
                columns.add(col);
                // Add to the Vector which maintains the Column order
                columnMap.put(col.getName(), col);
                // Add it to the map which allows fast lookup.
            }
        }
        catch (DuplicateColumnException e) {
            throw new IllegalStateException("Duplicate column: " + e.getMessage());
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("No such column: " + e.getMessage());
        }
        catch (InstantiationException e) {
            throw new IllegalStateException("Could not instantiate column: " + e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException("Illegal access on column: " + e.getMessage());
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Clone not supported: " + e.getMessage());
        }

    }


    /**
     * Copy Constructor for the ColumnSet object
     *
     * @param input ColumnSet to be used as a template for construction
     */
    public ColumnSet(ColumnSet input) {
        columns = new Vector<Column>();
        columnMap = new Hashtable<String, Column>();
        try {
            for (int j = 0; j < input.columns.size(); ++j) {
                Column col = input.columns.get(j);
                Column c;

                c = (Column) col.clone();

                columns.add(c);
                columnMap.put(c.getName(), c);
            }
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("CloneNotSupportedException thrown during construction.");
        }

    }

    @SuppressWarnings({"IfStatementWithTooManyBranches"})
    public ColumnSet(List<String> reqColumns, String text) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        columns = new Vector<Column>();
        columnMap = new Hashtable<String, Column>();
        int offset = 0;
        int length = text.length();
        for (String colStr : reqColumns) {
            Object obj = Class.forName(CLASS_PACKAGE + colStr).newInstance();
            Column col = (Column) obj;
            int width = col.getColumnWidth();
            if (offset + width <= length) {
                String tmp = text.substring(offset, offset + width).trim();
                if (col.getIsText()) {
                    col.setValue(new Variant(tmp));
                } else if (col.getIsIntType()) {
                    col.setValue(new Variant(Integer.parseInt(tmp)));
                } else if (col.getIsFloatType()) {
                    col.setValue(new Variant(Double.parseDouble(tmp)));
                } else if (col.getIsDateType()) {
                    TimeT tmpTime = TimeT.getTimeFromDateString(tmp);
                    // tmpTime has the time as GMT. The number of milliseconds may need adjustment for local time zone.
                    col.setValue(new Variant(new Date(tmpTime.getMilliseconds())));
                }
            }
            columns.add(col);
            columnMap.put(colStr, col);
            offset += (width + 1);
        }
    }


    /**
     * Returns the number of Columns in this ColumnSet.
     *
     * @return The number of Columns
     */
    public int length() {
        return columns.size();
    }


    /**
     * Returns a reference to the Column in the idx position
     *
     * @param idx index of the Column to retrieve
     * @return The returned Column reference.
     */
    public Column get(int idx) {
        return columns.get(idx);
    }

    /**
     * Returns a reference to the named Column
     *
     * @param name name of the Column to retrieve
     * @return The returned Column reference.
     */
    public Column get(String name) {
        return columnMap.get(name);
    }

    /**
     * Gets the names of all the Columns in this ColumnSet
     *
     * @return A Vector of Strings. ( The Column Names ).
     */
    public Vector<String> getColumnNames() {
        Vector<String> v = new Vector<String>();
        for (Column col : columns) {
            v.add(col.getName());
        }
        return v;
    }


    /**
     * Checks whether this ColumnSet contains the Column specified by name.
     *
     * @param name The Column name to check.
     * @return true if this ColumnSet has the Column specified by name.
     */
    public boolean hasColumn(String name) {
        return columnMap.containsKey(name);
    }


    /**
     * Determines whether this ColumnSet is a subset of another ColumnSet. The ColumnSet
     * is considered to be a subset of the input ColumnSet if for each Column in this
     * ColumnSet there is a Column in the input ColumnSet with the same name and the
     * same Column specifier.
     *
     * @param otherSet The input ColumnSet to be considered as a possible super set
     *                 of this ColumnSet.
     * @return true if this ColumnSet is a subset of the input ColumnSet
     */
    public boolean isSubset(ColumnSet otherSet) {

        for (Column col : columns) {
            if (otherSet.columnMap.containsKey(col.getName())) {
                Column col2 = otherSet.columnMap.get(col.getName());
                String s1 = col.getColumnSpecifier();
                String s2 = col2.getColumnSpecifier();
                if (!s1.equals(s2))
                    return false;
            } else return false;
        }
        return true;
    }


    /**
     * Tests whether this ColumnSet matches the input ColumnSet. Two ColumnSets match
     * if they have the same ColumnSpecifiers in the same order. The values stored
     * in the Columns are not considered in this test.
     *
     * @param otherSet The ColumnSet to be tested against this ColumnSet.
     * @return true if the two ColumnSets match.
     */
    public boolean matches(ColumnSet otherSet) {
        if (columns.size() != otherSet.columns.size())
            return false;
        for (Column col1 : columns) {
            Column col2 = otherSet.columnMap.get(col1.getName());
            String s1 = col1.getColumnSpecifier();
            String s2 = col2.getColumnSpecifier();
            if (!s1.equals(s2))
                return false;
        }
        return true;
    }


    /**
     * Sets the value of a named Column in this ColumnSet.
     *
     * @param name  The name of the Column whose value is to be set. If no Column in
     *              the ColumnSet has this name, then nothing is done.
     * @param value The new Column value
     */
    public void setValue(String name, Variant value) {
        Column col = columnMap.get(name);
        if (col != null)
            col.setValue(value);
    }

    /**
     * Sets all column values for this ColumnSet from a tab-delimited String whose tokens
     * are the String representations of the values to be set. All tokens must be in the
     * proper order and there must be the proper number of tokens as well.
     *
     * @param valuesString A tab-delimited String whose tokens contain the String representations
     *                     of the column values for this ColumnSet.
     */
    public void setValuesFromTabDelimitedString(String valuesString) {
        StringTokenizer st = new StringTokenizer(valuesString, "\t");
        int tokenCount = st.countTokens();
        if (tokenCount != columnMap.size()) {
            throw new IllegalArgumentException("Input values String has wrong number of tokens for this ColumnSet.");
        }

        for (Column column : columns) {
            String strValue = st.nextToken();
            column.setValueFromString(strValue);
        }
    }


    /**
     * Find out whether this Column contains an int (byte, short, int, long) value
     *
     * @param name The name of the Column to check.
     * @return true if the named Column holds an int.
     */
    public boolean IsIntType(String name) {
        Column col = columnMap.get(name);
        return col != null && col.getIsIntType();
    }


    /**
     * Find out whether this Column contains a float (float, double) value
     *
     * @param name The name of the Column to check.
     * @return true if the named Column holds a float.
     */
    public boolean isFloatType(String name) {
        Column col = columnMap.get(name);
        return col != null && col.getIsFloatType();
    }


    /**
     * Find out whether this Column contains text
     *
     * @param name The name of the Column to check.
     * @return true if the named Column holds text
     */
    public boolean isText(String name) {
        Column col = columnMap.get(name);
        return col != null && col.getIsText();
    }


    /**
     * Gets the value of a named column.
     *
     * @param name The name of the Column for which to retrieve a value. If no Column
     *             in this ColumnSet has this name, then returns null.
     * @return The value of the named Column
     */
    public Variant getValue(String name) {
        Column col = columnMap.get(name);
        if (col != null)
            return col.getValue();
        else
            return null;
    }


    /**
     * Returns true if this ColumnSet contains a single Column and that Column is stored
     * in the database as an integer and may have an associated sequence. This capability
     * supports primary key management through ColumnSets.
     *
     * @return true if this ColumnSet contains a single Column and that Column is
     *         stored in the database as an integer and may have an associated sequence.
     */
    public boolean getIsIntType() {
        return columns.size() == 1 && (columns.get(0)).getIsIntType();
    }


    /**
     * Get a String representation of the ColumnSet.
     *
     * @return A String representation of the values stored in this ColumnSet.
     */
    @Override
    public String toString() {
        return ValueString();
    }


    /**
     * Produce a String containing all the values of the ColumnSet, each properly aligned
     * within fixed-width columns.
     *
     * @return The String containing the Column values.
     */
    public String ValueString() {
        return makeConcatenatedString(new valueStringListGenerator(), " ");
    }


    /**
     * Return a comma-delimited list of Column names in this ColumnSet.
     *
     * @return The String containing the list of Column names.
     */
    public String ColumnNameString() {
        return makeConcatenatedString(new nameStringListGenerator(), ", ");
    }


    /**
     * Gets a String containing the parentheses-bounded SQL column description as used
     * in the CREATE TABLE command.
     *
     * @return The SQL String.
     */
    public String getCreateSqlString() {
        return makeConcatenatedString(new specifierStringListGenerator(), ", ");
    }


    /**
     * Gets a comma-delimited String of the ColumnSet values suitable for embedding
     * in an insert statement. All String values are enclosed in single-quotes.
     *
     * @return The SQL String
     */
    public String getInsertSqlString() {
        return makeConcatenatedString(new dbInsertStringListGenerator(), ", ");
    }


    public String getTabDelimitedString() {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < columns.size(); ++j) {
            Column col = columns.get(j);
            sb.append(col.getValue());
            if (j < columns.size() - 1)
                sb.append('\t');
        }
        return sb.toString();
    }


    /**
     * Compare two ColumnSet objects for equality.
     *
     * @param o The object to be compared for equality.
     * @return true if the contents of the two objects are identical.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (o instanceof ColumnSet) {
            // No need to check for null because instanceof handles that check
            ColumnSet s2 = (ColumnSet) o;
            if (s2.columns.size() != columns.size())
                return false;

            for (Column col1 : columns) {
                Column col2 = s2.columnMap.get(col1.getName());
                if (!col1.equals(col2))
                    return false;
            }
            return true;
        } else
            return false;
    }

    /**
     * Compare two ColumnSet objects for equality.  Based on the method equals()
     * The reference ColumnSet is a Mask - only desired elements of the ColumnSet are compared
     * Any default valued elements of the reference ColumnSet are ignored
     *
     * @param o The reference mask object to be compared for equality.
     * @return true if the desired elements of the two objects are identical.
     */
    public boolean equalsMask(Object o) {
        if (o == this)
            return true;

        if (o instanceof ColumnSet) {
            // No need to check for null because instanceof handles that check
            ColumnSet s2 = (ColumnSet) o;
            if (s2.columns.size() != columns.size())
                return false;

            for (Column col1 : columns) {
                Column col2 = s2.columnMap.get(col1.getName());
                /*  Here is where the method differs from the equals() method
                    the result is true if each element in column 1 equals that in column 2
                 OR if the element in column 2 is the default value
                */
                Variant defaultvalue = col2.getDefault(); // identify the default value of the column type
                Variant value = col2.getValue();          // identify the actual value in column 2
                if (!col1.equals(col2) && !value.equals(defaultvalue))
                    return false;
            }
            return true;
        } else
            return false;
    }

    /**
     * Returns an int hash code based on the contents of the ColumnSet.
     *
     * @return An int hashCode value.
     */
    @Override
    public int hashCode() {
        int code = 0;
        for (Column column : columns) code ^= column.hashCode();

        return code;
    }

    protected int getValueStringLength() {
        int result = 0;
        for (int j = 0; j < columns.size(); ++j) {
            Column c = columns.get(j);
            int width = c.getColumnWidth();
            if( c instanceof Lddate)
               width = 9;
            result += width;
            if (j < columns.size() - 1)
                result += 1;
        }

        return result;
    }

    /**
     * Reads the supplied line and "re-constructs" itself using the values in the String.
     * The line is assumed to be in the format that would be produced by the ValueString
     * method. This method and the ValueString method are, thus complementary, and form the
     * basis for reading and writing formatted files from collections of ColumnSets.
     *
     * @param line The String containing the new values
     * @throws ParseException Thrown if a lddate is incorrectly formatted
     */
    @SuppressWarnings({"IfStatementWithTooManyBranches"})
    public void parseString(final String line) throws ParseException {
        if (line.length() < getValueStringLength())
            throw new IllegalArgumentException("Supplied line (" + line + ") has wrong length for this ColumnSet!");

        int pos = 0;
        for (Column c : columns) {
            int columnWidth = c.getColumnWidth();
            String substr = line.substring(pos, Math.min(pos + columnWidth, line.length())).trim();
            if(substr.toUpperCase().equals("INF") || substr.toUpperCase().equals("NAN"))
                substr = "-999";
            if (c instanceof IntColumn) {
                int v = Integer.parseInt(substr);
                c.setValue(new Variant(v));
            } else if (c instanceof FloatColumn) {
                double v = Double.parseDouble(substr);
                c.setValue(new Variant(v));
            } else if (c instanceof StringColumn) {
                c.setValue(new Variant(substr));
            } else if (c instanceof Lddate) {
                try {
                    TimeT time = new TimeT(substr, "yyyy-MM-dd");
                    Date d = new Date(time.getMilliseconds());
                    c.setValue(new Variant(d));
                }
                catch (ParseException e) {
                    // do nothing
                }
            }
            pos += (columnWidth + 1);
        }

    }

    /**
     * Used to maintain an ordered collection of columns
     */
    protected Vector<Column> columns;
    /**
     * Used for rapid lookup of named columns
     */
    protected Hashtable<String, Column> columnMap;

    public static CssVersion getVersion() {
        return version;
    }

    public static void setVersion(CssVersion version) {
        ColumnSet.version = version;
    }


    private interface StringListGenerator {
        /**
         * Gets the columnString attribute of the StringListGenerator object
         *
         * @param col Description of the Parameter
         * @return The columnString value
         */
        public String getColumnString(Column col);
    }


    private static class nameStringListGenerator implements StringListGenerator {
        /**
         * Gets the columnString attribute of the nameStringListGenerator object
         *
         * @param col Description of the Parameter
         * @return The columnString value
         */
        public String getColumnString(Column col) {
            return col.getName();
        }
    }


    private static class specifierStringListGenerator implements StringListGenerator {
        /**
         * Gets the columnString attribute of the specifierStringListGenerator object
         *
         * @param col Description of the Parameter
         * @return The columnString value
         */
        public String getColumnString(Column col) {
            return col.getColumnSpecifier();
        }
    }


    private static class valueStringListGenerator implements StringListGenerator {
        /**
         * Gets the columnString attribute of the valueStringListGenerator object
         *
         * @param col Description of the Parameter
         * @return The columnString value
         */
        public String getColumnString(Column col) {
            return col.ValueString();
        }
    }


    private static class dbInsertStringListGenerator implements StringListGenerator {
        /**
         * Gets the columnString attribute of the dbInsertStringListGenerator object
         *
         * @param col Description of the Parameter
         * @return The columnString value
         */
        @Override
        public String getColumnString(Column col) {
            return col.DbInsertString();
        }
    }

    private String makeConcatenatedString(StringListGenerator b1, String separator) {
        StringBuilder s = new StringBuilder();
        for (int j = 0; j < columns.size(); ++j) {
            Column col = columns.get(j);
            s.append(b1.getColumnString(col));
            if (j < columns.size() - 1)
                s.append(separator);
        }
        return s.toString();
    }
}