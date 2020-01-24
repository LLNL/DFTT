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
