/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.waveform.continuous;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.waveform.filter.FilterClient;
import llnl.gnem.core.waveform.filter.StoredFilter;

/**
 *
 * @author dodge1
 */
public class BaseContinuousSeismogramModel implements ContinuousSeismogramModel, FilterClient{

    
    private final Collection<ContinuousSeismogramView> views;
    
    private final Collection<PairT<ContinuousSeismogram,ContinuousSeismogram>> seismograms;
    
    public BaseContinuousSeismogramModel()
    {
        views = new ArrayList<>();
        seismograms = new ArrayList<>();
    }
    @Override
    public void addView(ContinuousSeismogramView view) {
        views.add(view);
    }

    @Override
    public void addSeismogram(ContinuousSeismogram seismogram) {
        seismograms.add(new PairT<>(seismogram, new ContinuousSeismogram(seismogram)));
        notifyViewsSeismogramAdded(seismogram.getIdentifier());
    }

    @Override
    public ContinuousSeismogram getContinuousSeismogram(StreamKey key) {
        for( PairT<ContinuousSeismogram, ContinuousSeismogram> seisPair : seismograms){
            if( seisPair.getFirst().getIdentifier().equals(key)){
                return seisPair.getFirst();
            }
        }
        throw new IllegalArgumentException("Requested key " + key + " not found in collection!");
    }

    @Override
    public List<StreamKey> getSeismogramList() {
        List<StreamKey> result = new ArrayList<>();
        for (PairT<ContinuousSeismogram, ContinuousSeismogram> seisPair : seismograms) {
            result.add(seisPair.getFirst().getIdentifier());
        }
        return result;
    }

    @Override
    public void clear() {
        seismograms.clear();
        notifyViewsModelCleared();
    }

    @Override
    public void setSeismograms(Collection<ContinuousSeismogram> seismograms) {
        this.seismograms.clear();
       
        for( ContinuousSeismogram seis : seismograms){
            this.seismograms.add(new PairT<>(seis, new ContinuousSeismogram(seis)));
        }
        notifyViewsContentsReplaced();
    }

    private void notifyViewsModelCleared() {
        for( ContinuousSeismogramView view : views){
            view.clear();
        }
    }

    private void notifyViewsSeismogramAdded(StreamKey identifier) {
        for (ContinuousSeismogramView view : views) {
            view.seismogramWasAdded(identifier);
        }
    }

    private void notifyViewsContentsReplaced() {
        for (ContinuousSeismogramView view : views) {
            view.replaceContents();
        }
    }

    @Override
    public void applyFilter(StoredFilter filter) {
        for (PairT<ContinuousSeismogram, ContinuousSeismogram> seisPair : seismograms) {
            seisPair.getFirst().replaceContents(seisPair.getSecond());
            seisPair.getFirst().applyFilter(filter);
        }
        notifyViewsContentsReplaced();
    }

    @Override
    public void unApplyFilter() {
        for (PairT<ContinuousSeismogram, ContinuousSeismogram> seisPair : seismograms) {
            seisPair.getFirst().replaceContents(seisPair.getSecond());
        }
        notifyViewsContentsReplaced();
    }
    
}
