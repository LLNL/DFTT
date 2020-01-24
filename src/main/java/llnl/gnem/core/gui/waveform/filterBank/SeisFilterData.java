/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.filterBank;

import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.filter.StoredFilter;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */

@ThreadSafe
public class SeisFilterData {
    private final CssSeismogram seismogram;
    private final StoredFilter filter;

    public SeisFilterData(CssSeismogram seis,
    StoredFilter filter)
    {
        this.seismogram = seis;
        this.filter = filter;
    }

    public CssSeismogram getSeismogram() {
        return seismogram;
    }

    public StoredFilter getFilter() {
        return filter;
    }
}
