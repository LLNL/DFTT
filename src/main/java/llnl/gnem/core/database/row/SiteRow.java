package llnl.gnem.core.database.row;

import llnl.gnem.core.util.Variant;

import java.util.Vector;
import java.sql.*;

/*

 *  COPYRIGHT NOTICE

 *  GnemUtils Version 1.0

 *  Copyright (C) 2002 Lawrence Livermore National Laboratory.

 */
/**
 * A class that manages access to a single row retrieved from the SITE table.
 *
 * @author Doug Dodge
 */
public class SiteRow extends ColumnSet {

    /**
     * Default Constructor for the SiteRow object
     */
    public SiteRow() {
        super(getMyColumnNames());
    }

    /**
     * Constructor for the SiteRow object that fully specifies all Column values
     * in the row.
     *
     * @param sta Station name
     * @param ondate Station ondate
     * @param offdate Station offdate
     * @param lat Station latitude
     * @param lon Station longitude
     * @param elev Station elevation
     * @param staname Descriptive station name
     * @param statype Station type ( 'ss', 'ar' )
     * @param refsta Reference station name for this station
     * @param dnorth Northing in km relative to reference station
     * @param deast Easting in km relative to refsta
     * @param lddate Date this row was created
     */
    public SiteRow(String sta, int ondate, int offdate, double lat, double lon, double elev, String staname, String statype, String refsta, double dnorth, double deast, Date lddate) {
        super(getMyColumnNames());
        setValue("Sta", new Variant(sta));
        setValue("Ondate", new Variant(ondate));
        setValue("Offdate", new Variant(offdate));
        setValue("Lat", new Variant(lat));
        setValue("Lon", new Variant(lon));
        setValue("Elev", new Variant(elev));
        setValue("Staname", new Variant(staname));
        setValue("Statype", new Variant(statype));
        setValue("Refsta", new Variant(refsta));
        setValue("Dnorth", new Variant(dnorth));
        setValue("Deast", new Variant(deast));
        setValue("Lddate", new Variant(lddate));
    }

    /**
     * Creates a new WfdiscRow from a ColumnSet object. The ColumnSet is assumed
     * to have all the fields of the WfdiscRow. If not an exception will be
     * thrown.
     *
     * @param cs The ColumnSet to serve as the source for the new WfdiscRow.
     * @return The newly-constructed WfdiscRow.
     */
    public static SiteRow createFromColumnSet(ColumnSet cs) {
        return new SiteRow(cs.getValue("Sta").toString(),
                cs.getValue("Ondate").intValue(),
                cs.getValue("Offdate").intValue(),
                cs.getValue("Lat").doubleValue(),
                cs.getValue("Lon").doubleValue(),
                cs.getValue("Elev").doubleValue(),
                cs.getValue("Staname").toString(),
                cs.getValue("Statype").toString(),
                cs.getValue("Refsta").toString(),
                cs.getValue("Dnorth").doubleValue(),
                cs.getValue("Deast").doubleValue(),
                cs.getValue("Lddate").dateValue()
        );
    }

    public void setSta(String sta) {
        setValue("Sta", new Variant(sta));
    }

    public void setOndate(int ondate) {
        setValue("Ondate", new Variant(ondate));
    }

    public void setOffdate(int offdate) {
        setValue("Offdate", new Variant(offdate));
    }

    public void setLat(double lat) {
        setValue("Lat", new Variant(lat));
    }

    public void setLon(double lon) {
        setValue("Lon", new Variant(lon));
    }

    public void setElev(double elev) {
        setValue("Elev", new Variant(elev));
    }

    public void setStaname(String staname) {
        setValue("Staname", new Variant(staname));
    }

    public void setStatype(String statype) {
        setValue("Statype", new Variant(statype));
    }

    public void setRefsta(String refsta) {
        setValue("Refsta", new Variant(refsta));
    }

    public void setDnorth(double dnorth) {
        setValue("Dnorth", new Variant(dnorth));
    }

    public void setDeast(double deast) {
        setValue("Deast", new Variant(deast));
    }

    public void setLddate(Date lddate) {
        setValue("Lddate", new Variant(lddate));
    }

    public String getSta() {
        return getValue("Sta").toString();
    }

    public int getOndate() {
        return getValue("Ondate").intValue();
    }

    public int getOffdate() {
        return getValue("Offdate").intValue();
    }

    public double getLat() {
        return getValue("Lat").doubleValue();
    }

    public double getLon() {
        return getValue("Lon").doubleValue();
    }

    public double getElev() {
        return getValue("Elev").doubleValue();
    }

    public String getStaname() {
        return getValue("Staname").toString();
    }

    public String getStatype() {
        return getValue("Statype").toString();
    }

    public String getRefsta() {
        return getValue("Refsta").toString();
    }

    public double getDnorth() {
        return getValue("Dnorth").doubleValue();
    }

    public double getDeast() {
        return getValue("Deast").doubleValue();
    }

    public Date getLddate() {
        return getValue("Lddate").dateValue();
    }

    /**
     * Gets the Column Names of a SiteRow class
     *
     * @return A Vector of Strings containing the Column Names of a SiteRow.
     */
    public static Vector<String> getMyColumnNames() {
        Vector<String> result = new Vector<String>();
        for (int j = 0; j < names.length; ++j) {
            result.add(names[j]);
        }
        return result;
    }

    /**
     * Gets a Vector of Strings containing the SiteRow primary key Columns.
     *
     * @return The primary key Columns Vector.
     */
    public static Vector<String> getPkColumns() {
        Vector<String> pkColumns = new Vector<String>();
        pkColumns.add("Sta");
        pkColumns.add("Ondate");
        return pkColumns;
    }

    private final static String[] names = {"Sta", "Ondate", "Offdate", "Lat", "Lon", "Elev", "Staname", "Statype", "Refsta", "Dnorth", "Deast", "Lddate"};
}
