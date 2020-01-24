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
