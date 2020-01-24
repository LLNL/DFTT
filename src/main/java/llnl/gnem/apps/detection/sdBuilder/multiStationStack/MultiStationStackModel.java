/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.multiStationStack;

import java.util.ArrayList;
import java.util.Collection;
import llnl.gnem.core.waveform.filter.FilterClient;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class MultiStationStackModel implements FilterClient{
    
    private final Collection<StackElement> stacks;
    private MultiStationStackPlot viewer;
    private MultiStationStackModel() {
        stacks = new ArrayList<>();
    }
    
    public static MultiStationStackModel getInstance() {
        return MultiStationStackModelHolder.INSTANCE;
    }
    
    public void clear()
    {
        stacks.clear();
        notifyViewsDataChanged();
    }

    public void setStackData(Collection<StackElement> stacks) {
        clear();
        this.stacks.addAll(stacks);
        notifyViewsDataChanged();
    }
    
    public Collection<StackElement> getStackData()
    {
        return new ArrayList<>(stacks);
    }

    private void notifyViewsDataChanged() {
        if( viewer != null){
            viewer.updateForChangedData();
        }
    }

    void addView(MultiStationStackPlot viewer) {
        this.viewer = viewer;
    }

    @Override
    public void applyFilter(StoredFilter filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unApplyFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static class MultiStationStackModelHolder {

        private static final MultiStationStackModel INSTANCE = new MultiStationStackModel();
    }
}
