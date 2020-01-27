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
package llnl.gnem.core.metadata.site.core;

import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author ganzberger1
 */
@ThreadSafe
public class CssSite extends Site {

    private final String staname;
    private final String statype;
    private final String refsta;
    private final double dnorth;
    private final double deast;

    public CssSite(String sta, int ondate, int offdate, double lat, double lon,
            double elev, String staname, String statype, String refsta,
            double dnorth, double deast) {
        super(sta,
                sta,
                lat,
                lon,
                elev, ondate, offdate);
        this.staname = staname;
        this.statype = statype;
        this.refsta = refsta;
        this.dnorth = dnorth;
        this.deast = deast;

    }

    public CssSite(String sta, int ondate, int offdate, double lat, double lon,
            double elev, String staname, String statype, String refsta,
            double dnorth, double deast, int siteid) {
        super(sta,
                sta,
                lat,
                lon,
                elev, ondate, offdate, siteid);
        this.staname = staname;
        this.statype = statype;
        this.refsta = refsta;
        this.dnorth = dnorth;
        this.deast = deast;

    }

    @Override
    public String toString() {
        return String.format("%d, %s, %d, %d, %f, %f, %f, %s ",
                getStationId(), getSta(), getOndate(), getOffdate(),
                getLat(), getLon(), getElevation(), getStaname());

    }

    /**
     * @return the staname
     */

    public String getStaname() {
        return staname;
    }

    /**
     * @return the statype
     */
    public String getStatype() {
        return statype;
    }

    /**
     * @return the refsta
     */
    public String getRefsta() {
        return refsta;
    }

    /**
     * @return the dnorth
     */
    public double getDnorth() {
        return dnorth;
    }

    /**
     * @return the deast
     */
    public double getDeast() {
        return deast;
    }

    public boolean isParametersMatch(CssSite other) {
        if (getLat() != other.getLat()) {
            return false;
        }
        if (getLon() != other.getLon()) {
            return false;
        }
        if (getElevation() != other.getElevation()) {
            return false;
        }
        if (getOndate() != other.getOndate()) {
            return false;
        }
        if (getOffdate() != other.getOffdate()) {
            return false;
        }
        if (dnorth != other.dnorth) {
            return false;
        }
        if (deast != other.deast) {
            return false;
        }
        if (!staname.equals(other.staname)) {
            return false;
        }
        if (!statype.equals(other.statype)) {
            return false;
        }
        if (!refsta.equals(other.refsta)) {
            return false;
        }
        return true;
    }


    public String getHtmlString() {
        StringBuilder sb = new StringBuilder("<html>");
        sb.append(String.format("STA: %s",getSta()));
        sb.append("<br>");
        sb.append(String.format("LAT: %8.4f",getLat()));
        sb.append("<br>");
        sb.append(String.format("LON: %8.4f",getLon()));
        sb.append("<br>");
        sb.append(String.format("ELEV: %6.3f",getElevation()));
        sb.append("<br>");
        sb.append(String.format("ONDATE: %d",getOndate()));
        sb.append("<br>");
        sb.append(String.format("OFFDATE: %d",getOffdate()));
        sb.append("<br>");

        sb.append(String.format("STANAME: %s",staname));
        sb.append("<br>");
        sb.append(String.format("STATYPE: %s",statype));
        sb.append("<br>");
        sb.append(String.format("REFSTA: %s", refsta));
        sb.append("<br>");
        sb.append(String.format("DNORTH: %8.4f",dnorth));
        sb.append("<br>");
        sb.append(String.format("DEAST: %8.4f",deast));
        sb.append("</html>");

        return sb.toString();
    }
    
    public boolean isEffective(int jdate) {
        return getOndate() <= jdate && getOffdate() >= jdate;
    }
    
    public boolean isArrayRefsta()
    {
        return getStatype().equals("ar") && getSta().equals(getRefsta());
    }
    
    public boolean isNonRefstaArrayElement()
    {
        return getStatype().equals("ss") && !getSta().equals(getRefsta());
    }
}
