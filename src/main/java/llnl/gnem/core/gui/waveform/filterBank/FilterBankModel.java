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
package llnl.gnem.core.gui.waveform.filterBank;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.gui.filter.FilterModel;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.gui.waveform.WaveformDataModel;
import llnl.gnem.core.waveform.seismogram.CssSeismogram;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class FilterBankModel extends WaveformDataModel<BaseFilterBankView> {
    BaseSingleComponent component;

    private FilterBankModel() {
    }

    public static FilterBankModel getInstance() {
        return FilterBankModelHolder.instance;
    }

    private static class FilterBankModelHolder {
        private static final FilterBankModel instance = new FilterBankModel();
    }

    @Override
    public void clear() {
        for (BaseFilterBankView view : getViews()) {
            view.clear();
        }
    }

    void applyFilter(StoredFilter filter) {
        WaveformViewerFactoryHolder.getInstance().getThreeComponentModel().applyFilter(filter);
        WaveformViewerFactoryHolder.getInstance().getThreeComponentModel().setActive(true);
    }

    public void setComponent(BaseSingleComponent aComp) {
        Collection<SeisFilterData> data = new ArrayList<SeisFilterData>();
        component = new BaseSingleComponent(aComp);
        Collection<StoredFilter> usable = buildFilterList();
        for (StoredFilter sf : usable) {
            CssSeismogram seis = new CssSeismogram(component.getSeismogram());
            seis.removeTrend();
            seis.Taper(5.0);
            seis.applyFilter(sf);
            data.add(new SeisFilterData(seis, sf));
        }

        for (BaseFilterBankView view : getViews()) {
            view.setData(data, aComp);
        }
    }

    private Collection<StoredFilter> buildFilterList() {
        double nyquist = component.getSeismogram().getSamprate() / 2;
        ArrayList<StoredFilter> filters = FilterModel.getInstance().getAllStoredFilters();
        Collection<StoredFilter> usable = new ArrayList<StoredFilter>();
        for (StoredFilter filter : filters) {
            if (filter.getHighpass() < nyquist) {
                usable.add(filter);
            }
        }
        return usable;
    }
}
