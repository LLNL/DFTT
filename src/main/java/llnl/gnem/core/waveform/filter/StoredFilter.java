/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.waveform.filter;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Objects;
import llnl.gnem.core.util.Passband;

/**
 *
 * @author dodge1
 */
public class StoredFilter implements Serializable{

    private static final long serialVersionUID = 1460609266314829552L;

    private final int filterid;
    private final Passband passband;
    private final boolean causal;
    private final int order;
    private final double lowpass;
    private final double highpass;
    private final String descrip;
    private final String impulseResponse;
    private final String auth;
    private final boolean defaultFilter;

    public StoredFilter(int filterid,
            Passband passband,
            boolean causal,
            int order,
            double lowpass,
            double highpass,
            String descrip,
            String impulseResponse,
            String auth,
            boolean isDefault) {
        this.filterid = filterid;
        this.passband = passband;
        this.causal = causal;
        this.order = order;
        this.lowpass = lowpass;
        this.highpass = highpass;
        this.descrip = descrip;
        this.impulseResponse = impulseResponse;
        this.auth = auth;
        defaultFilter = isDefault;
    }

    public StoredFilter() {
        this.filterid = -1;
        this.passband = Passband.BAND_PASS;
        this.causal = false;
        this.order = 2;
        this.lowpass = 0.0001;
        this.highpass = 1000;
        this.descrip = "-";
        this.impulseResponse = "iir";
        this.auth = "-";
        defaultFilter = false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.passband);
        hash = 59 * hash + (this.causal ? 1 : 0);
        hash = 59 * hash + this.order;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.lowpass) ^ (Double.doubleToLongBits(this.lowpass) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.highpass) ^ (Double.doubleToLongBits(this.highpass) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StoredFilter other = (StoredFilter) obj;
        if (!Objects.equals(this.passband, other.passband)) {
            return false;
        }
        if (this.causal != other.causal) {
            return false;
        }
        if (this.order != other.order) {
            return false;
        }
        if (Double.doubleToLongBits(this.lowpass) != Double.doubleToLongBits(other.lowpass)) {
            return false;
        }
        if (Double.doubleToLongBits(this.highpass) != Double.doubleToLongBits(other.highpass)) {
            return false;
        }
        return true;
    }

    public int getFilterid() {
        return filterid;
    }

    public Passband getPassband() {
        return passband;
    }

    public boolean isCausal() {
        return causal;
    }

    public int getOrder() {
        return order;
    }

    public double getLowpass() {
        return lowpass;
    }

    public double getHighpass() {
        return highpass;
    }

    public String getDescrip() {
        return descrip;
    }

    public String getImpulseResponse() {
        return impulseResponse;
    }

    public String getAuth() {
        return auth;
    }

    public boolean isDefaultFilter() {
        return defaultFilter;
    }

    @Override
    public String toString() {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(3);
        StringBuilder result = new StringBuilder(passband.toString() + "_LC_");
        result.append(f.format(lowpass)).append("_HC_");
        result.append(f.format(highpass));
        String tmp = causal ? "_Causal_Order_" : "_Acausal_Order_";
        result.append(tmp);
        result.append(order);
        return result.toString();
    }

    public boolean isFunctionallyEquivalent(StoredFilter filter) {
        if (!this.passband.equals(filter.passband)) {
            return false;
        }
        if (this.causal != filter.causal) {
            return false;
        }
        if (this.order != filter.order) {
            return false;
        }
        if (this.lowpass != filter.lowpass) {
            return false;
        }
        if (this.highpass != filter.highpass) {
            return false;
        }
        return this.impulseResponse.equals(filter.impulseResponse);

    }

}
