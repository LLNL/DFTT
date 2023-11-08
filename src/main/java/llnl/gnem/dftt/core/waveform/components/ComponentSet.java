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
package llnl.gnem.dftt.core.waveform.components;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import llnl.gnem.dftt.core.seismicData.AbstractEventInfo;
import llnl.gnem.dftt.core.gui.waveform.ArrivalListener;
import llnl.gnem.dftt.core.gui.waveform.ComponentSetPlot;
import llnl.gnem.dftt.core.gui.waveform.ComponentSetPlotHolder;
import llnl.gnem.dftt.core.gui.waveform.DisplayArrival;
import llnl.gnem.dftt.core.util.ApplicationLogger;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.PairT;
import llnl.gnem.dftt.core.waveform.filter.StoredFilter;
import llnl.gnem.dftt.core.waveform.responseProcessing.WaveformDataType;

public class ComponentSet<T extends BaseSingleComponent> implements Comparable<ComponentSet> {
    
    public static final double ORTHOGONAL_COSINE_THRESHOLD = 0.001;
    private final ArrivalManager arrivalManager;
    protected final ArrayList<T> uncategorized;
    protected T verticalComponent;
    protected PairT<T, T> horizontalComponents;
    
    public ComponentSet() {
        uncategorized = new ArrayList<>();
        arrivalManager = new ArrivalManager();
    }
    
    public ComponentSet(Collection<T> components) throws ComponentSetException {
        uncategorized = new ArrayList<>();
        arrivalManager = new ArrivalManager();
        components.forEach((bsc) -> {
            this.add(bsc);
        });
        this.ensureComponentTimePeriodsMatch();
    }
    
    public ComponentSet(T component) {
        uncategorized = new ArrayList<>();
        arrivalManager = new ArrivalManager();
        this.add(component);
    }
    
    public ComponentSet(ComponentSet<T> set) {
        this(set, set.getType());
    }
    
    public ComponentSet(ComponentSet<T> set, ComponentType type) {
        uncategorized = new ArrayList<>(set.uncategorized);
        this.arrivalManager = new ArrivalManager(set.arrivalManager);
        if (set.verticalComponent != null) {
            verticalComponent = (T) set.verticalComponent.copy(type);
//            arrivalManager.addArrivalListener(verticalComponent);
        }
        if (set.horizontalComponents != null) {
            T comp1 = (T) set.horizontalComponents.getFirst().copy(type);
            T comp2 = (T) set.horizontalComponents.getSecond().copy(type);
//            arrivalManager.addArrivalListener(comp1);
//            arrivalManager.addArrivalListener(comp2);
            horizontalComponents = new PairT<>(comp1, comp2);
        }
    }
    
    public boolean containsComponent(T component) {
        if (component == null) {
            return false;
        } else if (component == verticalComponent) {
            return true;
        } else if (horizontalComponents != null) {
            if (component == horizontalComponents.getFirst() || component == horizontalComponents.getSecond()) {
                return true;
            } else {
                return uncategorized.contains(component);
            }
        }
        return false;
    }
    
    public void forceCommonTraceLength() {
        Collection<T> components = getComponentCollection();
        Epoch common = new Epoch(-Double.MAX_VALUE, Double.MAX_VALUE);
        for (T comp : components) {
            Epoch e = comp.getTraceData().getEpoch();
            common = common.getIntersection(e);
        }
        for (T comp : components) {
            comp.trimTo(common);
        }
    }
    
    public void addArrivalListener(ArrivalListener listener) {
        arrivalManager.addArrivalListener(listener);
    }
    
    public void removeArrivalListener(ArrivalListener listener) {
        arrivalManager.removeArrivalListener(listener);
    }
    
    public DisplayArrival createPick(BaseSingleComponent component, double relTime, double refTime, String phase, String auth) {
        double epochTime = relTime + refTime;
        double stdErr = component.estimatePickStdErr(epochTime);
        DisplayArrival arrival = createArrival(component, relTime, refTime, stdErr, phase, auth);
        return arrival;
    }
    
    protected DisplayArrival createArrival(BaseSingleComponent component, double relTime, double refTime, double stdErr,
            String phase, String auth) {
        return new DisplayArrival(relTime, refTime, stdErr, phase, auth, component.getIdentifier());
    }
    
    public void addArrival(T component, DisplayArrival arrival) {
        arrivalManager.addArrival(component, arrival);
    }
    
    public T deleteArrival(DisplayArrival arrival) {
        return arrivalManager.removeArrival(arrival);
    }
    
    public Collection<DisplayArrival> getArrivals() {
        return arrivalManager.getArrivals();
    }
    
    public Collection<PairT<T, DisplayArrival>> getActiveArrivals() {
        Collection<PairT<T, DisplayArrival>> active = new ArrayList<>();
        for (ArrivalState state : arrivalManager.arrivals.values()) {
            if (!state.isDeleted()) {
                active.add(new PairT<>(state.getComponent(), state.getArrival()));
            }
        }
        return active;
    }
    
    public boolean deletedArrival(DisplayArrival arrival) {
        return arrivalManager.arrivals.get(arrival).isDeleted();
    }
    
    public Collection<DisplayArrival> getDeletedArrivals() {
        Collection<DisplayArrival> deleted = new ArrayList<>();
        for (ArrivalState state : arrivalManager.arrivals.values()) {
            if (state.isDeleted()) {
                deleted.add(state.getArrival());
            }
        }
        return deleted;
    }
    
    public void removeDeletedArrivals() {
        arrivalManager.removeDeletedArrivals();
    }
    
    public boolean hasArrival(T component, String phase) {
        return arrivalManager.hasArrival(component, phase);
    }
    
    public boolean isArrivalSaveRequired() {
        return arrivalManager.isSaveRequired();
    }
    
    public T getComponent(ComponentIdentifier identifier) {
        for (T component : getComponents()) {
            if (component.getIdentifier().isCompatibleWith(identifier)) {
                return component;
            }
        }
        throw new IllegalStateException("No compatible component found for identifier " + identifier);
    }
    
    public T getMatchingComponent(ComponentIdentifier identifier) {
        for (T component : getComponents()) {
            if (component.getIdentifier().equals(identifier)) {
                return component;
            }
        }
        return null;
    }
    
    public Collection<T> getTransferableComponents() {
        return getComponents();
    }
    
    public boolean horizontalComponentsAvailable() {
        return hasHorizontalComponents();
    }
    
    public Collection<T> getComponents() {
        Collection<T> components = new ArrayList<>();
        if (hasVerticalComponent()) {
            components.add(verticalComponent);
        }
        if (horizontalComponentsAvailable()) {
            components.add(horizontalComponents.getFirst());
            components.add(horizontalComponents.getSecond());
        }
        for (T component : uncategorized) {
            components.add(component);
        }
        return components;
    }
    
    public T getVerticalComponent() {
        return verticalComponent;
    }
    
    public T getHorizontalComponent1() {
        if (horizontalComponents != null) {
            return horizontalComponents.getFirst();
        } else {
            return null;
        }
    }
    
    public T getHorizontalComponent2() {
        if (horizontalComponents != null) {
            return horizontalComponents.getSecond();
        } else {
            return null;
        }
    }
    
    public boolean hasUncategorizedComponent() {
        return !uncategorized.isEmpty();
    }
    
    public int getUncategorizedComponentCount() {
        return uncategorized.size();
    }
    
    public T getUncategorizedComponent(int idx) {
        return uncategorized.get(idx);
    }
    
    public ComponentType getType() {
        if (hasVerticalComponent()) {
            return verticalComponent.getIdentifier().getType();
        } else if (horizontalComponents != null) {
            return horizontalComponents.getFirst().getIdentifier().getType();
        } else if (!uncategorized.isEmpty()) {
            return uncategorized.iterator().next().getIdentifier().getType();
        } else {
            return null;
        }
    }
    
    public void convertToType(WaveformDataType newType) throws SQLException, IOException {
        if (hasVerticalComponent()) {
            verticalComponent.convertToType(newType);
        }
        if (horizontalComponents != null) {
            horizontalComponents.getFirst().convertToType(newType);
            horizontalComponents.getSecond().convertToType(newType);
        }
        if (!uncategorized.isEmpty()) {
            for (T aComp : uncategorized) {
                aComp.convertToType(newType);
            }
        }
    }
    
    public void rotateToGCP(AbstractEventInfo info) {
        if (hasHorizontalComponents() && canBeRotated(horizontalComponents)) {
            PairT<BaseSingleComponent, BaseSingleComponent> rotatedComponents = BaseSingleComponent.rotateToGcp(horizontalComponents, info);
            horizontalComponents.getFirst().updateFrom(rotatedComponents.getFirst());
            horizontalComponents.getSecond().updateFrom(rotatedComponents.getSecond());
        }
    }
    
    public final void ensureComponentTimePeriodsMatch() throws ComponentSetException {
        Epoch intersectionEpoch = null;
        if (verticalComponent != null) {
            intersectionEpoch = verticalComponent.getEpoch();
        }
        if (horizontalComponents != null) {
            T c1 = horizontalComponents.getFirst();
            if (intersectionEpoch == null) {
                intersectionEpoch = c1.getEpoch();
            } else {
                intersectionEpoch = intersectionEpoch.intersection(c1.getEpoch());
            }
            T c2 = horizontalComponents.getSecond();
            intersectionEpoch = intersectionEpoch.intersection(c2.getEpoch());
        }
        if (intersectionEpoch == null || intersectionEpoch.isEmpty()) {
            throw new ComponentSetException("Empty intersection among components!");
        }
        
        if (verticalComponent != null) {
            verticalComponent.trimTo(intersectionEpoch);
        }
        if (horizontalComponents != null) {
            T c1 = horizontalComponents.getFirst();
            c1.trimTo(intersectionEpoch);
            T c2 = horizontalComponents.getSecond();
            c2.trimTo(intersectionEpoch);
        }
    }
    
    public void removeTrend() {
        if (verticalComponent != null) {
            verticalComponent.removeTrend();
        }
        if (horizontalComponents != null) {
            horizontalComponents.getFirst().removeTrend();
            horizontalComponents.getSecond().removeTrend();
        }
        for (T bsc : uncategorized) {
            bsc.removeTrend();
        }
    }
    
    public void removeMean() {
        if (verticalComponent != null) {
            verticalComponent.removeMean();
        }
        if (horizontalComponents != null) {
            horizontalComponents.getFirst().removeMean();
            horizontalComponents.getSecond().removeMean();
        }
        for (T bsc : uncategorized) {
            bsc.removeMean();
        }
    }
    
    public void applyTaper(double taperPercent) {
        if (verticalComponent != null) {
            verticalComponent.applyTaper(taperPercent);
        }
        if (horizontalComponents != null) {
            horizontalComponents.getFirst().applyTaper(taperPercent);
            horizontalComponents.getSecond().applyTaper(taperPercent);
        }
        for (T bsc : uncategorized) {
            bsc.applyTaper(taperPercent);
        }
        
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int numComponents = 0;
        if (hasVerticalComponent()) {
            ++numComponents;
            sb.append(verticalComponent.getIdentifier());
        }
        
        if (hasHorizontalComponents()) {
            numComponents += 2;
            sb.append(", ");
            
            sb.append(horizontalComponents.getFirst().getIdentifier());
            sb.append(", ");
            sb.append(horizontalComponents.getSecond().getIdentifier());
            
        }
        
        if (hasUncategorizedComponent()) {
            numComponents += uncategorized.size();
            for (T bsc : uncategorized) {
                sb.append(", ");
                sb.append(bsc.getIdentifier());
            }
        }
        return String.format("%d-component set with components %s", numComponents, sb.toString());
    }
    
    private boolean hasHorizontalComponents() {
        return horizontalComponents != null;
    }
    
    private boolean hasVerticalComponent() {
        return verticalComponent != null;
    }

    /**
     * The add method adds a single component to the ComponentSet. In the case
     * where the new component is a vertical component, it simply assigns the
     * new component to the internal verticalComponent. When the new component
     * is a horizontal component and it is the second horizontal component to be
     * added then the two horizontal components are compared to see which
     * ordering will make the angle measured clockwise from comp1 to comp2 to be
     * positive. They are then sorted into that order. In the usual case, this
     * will result in the N-component becoming component 1 and the E component
     * becoming component2.
     *
     * @param component The component to be added.
     */
    public final void add(T component) {
        if (component.getOrientation() == ComponentOrientation.VERTICAL || component.getOrientation() == ComponentOrientation.UNDEFINED) {
            verticalComponent = component;
        } else if (component.getOrientation() == ComponentOrientation.HORIZONTAL) {
            uncategorized.add(component);
        }
//        addArrivalListener(component);

        if (uncategorized.size() == 2) {
            T c1 = uncategorized.get(0);
            T c2 = uncategorized.get(1);
            double theta1 = Math.toRadians(90 - c1.getAzimuth());
            double theta2 = Math.toRadians(90 - c2.getAzimuth());
            double crossProdZValue = Math.sin(theta1) * Math.cos(theta2) - Math.sin(theta2) * Math.cos(theta1);
            double test = 1 - Math.abs(crossProdZValue);
            if (test < ORTHOGONAL_COSINE_THRESHOLD) {
                if (crossProdZValue > 0) {
                    horizontalComponents = new PairT<>(c1, c2);
                } else {
                    horizontalComponents = new PairT<>(c2, c1);
                }
                uncategorized.remove(c1);
                uncategorized.remove(c2);
            }
            
        }
    }
    
    public int getComponentCount() {
        int count = 0;
        if (verticalComponent != null) {
            ++count;
        }
        if (horizontalComponents != null) {
            count += 2;
        }
        return count + uncategorized.size();
    }
    
    public boolean canRotateComponents() {
        return hasHorizontalComponents() && canBeRotated(horizontalComponents);
    }
    
    @SuppressWarnings({"AssignmentToNull"})
    public void replaceContentsFromBackup(ComponentSet<T> set) {
        if (set.verticalComponent != null) {
            verticalComponent.updateFrom(set.verticalComponent);
        } else {
            if (verticalComponent != null) {
//                removeArrivalListener(verticalComponent);
            }
            verticalComponent = null;
        }
        if (set.horizontalComponents != null) {
            horizontalComponents.getFirst().updateFrom(set.horizontalComponents.getFirst());
            horizontalComponents.getSecond().updateFrom(set.horizontalComponents.getSecond());
        } else {
            if (horizontalComponents != null) {
//                removeArrivalListener(horizontalComponents.getFirst());
//                removeArrivalListener(horizontalComponents.getSecond());
            }
            horizontalComponents = null;
        }
        for (int j = 0; j < uncategorized.size(); ++j) {
            uncategorized.get(j).updateFrom(set.uncategorized.get(j));
        }
    }
    
    public boolean containsComponent(ComponentSet set) {
        Collection<ComponentIdentifier> myIdentifiers = getIdentifiers();
        Collection<ComponentIdentifier> otherIdentifiers = set.getIdentifiers();
        for (ComponentIdentifier id : myIdentifiers) {
            for (ComponentIdentifier otherId : otherIdentifiers) {
                if (id.equals(otherId)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Collection<ComponentIdentifier> getIdentifiers() {
        Collection<ComponentIdentifier> result = new ArrayList<>();
        if (verticalComponent != null) {
            result.add(verticalComponent.getIdentifier());
        }
        if (horizontalComponents != null) {
            result.add(horizontalComponents.getFirst().getIdentifier());
            result.add(horizontalComponents.getSecond().getIdentifier());
        }
        for (T bsc : uncategorized) {
            result.add(bsc.getIdentifier());
        }
        return result;
    }
    
    public boolean isDuplicate(ComponentSet other) {
        
        if (!uncategorizedAreDupliates(other)) {
            return false;
        }
        if (!verticalsAreDuplicates(other)) {
            return false;
        }
        
        return horizontalsAreDuplicates(other);
    }
    
    public void integrate() {
        if (verticalComponent != null && verticalComponent.canIntegrate()) {
            verticalComponent.integrate();
        }
        if (horizontalComponents != null) {
            if (horizontalComponents.getFirst().canIntegrate()) {
                horizontalComponents.getFirst().integrate();
            }
            if (horizontalComponents.getSecond().canIntegrate()) {
                horizontalComponents.getSecond().integrate();
            }
        }
        for (T bsc : uncategorized) {
            if (bsc.canIntegrate()) {
                bsc.integrate();
            }
        }
    }
    
    public void differentiate() {
        if (verticalComponent != null && verticalComponent.canDifferentiate()) {
            verticalComponent.differentiate();
        }
        if (horizontalComponents != null) {
            if (horizontalComponents.getFirst().canDifferentiate()) {
                horizontalComponents.getFirst().differentiate();
            }
            if (horizontalComponents.getSecond().canDifferentiate()) {
                horizontalComponents.getSecond().differentiate();
            }
            
        }
        for (T bsc : uncategorized) {
            if (bsc.canDifferentiate()) {
                bsc.differentiate();
            }
        }
    }
    
    public boolean canIntegrate() {
        
        if (verticalComponent != null) {
            if (verticalComponent.canIntegrate()) {
                return true;
            }
        }
        if (horizontalComponents != null) {
            if (horizontalComponents.getFirst().canIntegrate()) {
                return true;
            }
            if (horizontalComponents.getSecond().canIntegrate()) {
                return true;
            }
        }
        for (T bsc : uncategorized) {
            if (bsc.canIntegrate()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean canDifferentiate() {
        
        if (verticalComponent != null) {
            if (verticalComponent.canDifferentiate()) {
                return true;
            }
        }
        if (horizontalComponents != null) {
            if (horizontalComponents.getFirst().canDifferentiate()) {
                return true;
            }
            if (horizontalComponents.getSecond().canDifferentiate()) {
                return true;
            }
        }
        for (T bsc : uncategorized) {
            if (bsc.canDifferentiate()) {
                return true;
            }
        }
        return false;
    }
    
    public void applyFilter(StoredFilter filter) {
        if (verticalComponent != null) {
            verticalComponent.applyFilter(filter);
        }
        if (horizontalComponents != null) {
            horizontalComponents.getFirst().applyFilter(filter);
            horizontalComponents.getSecond().applyFilter(filter);
        }
        for (T bsc : uncategorized) {
            bsc.applyFilter(filter);
        }
    }
    
    public void unApplyFilter() {
        if (verticalComponent != null) {
            verticalComponent.unApplyFilter();
        }
        if (horizontalComponents != null) {
            horizontalComponents.getFirst().unApplyFilter();
            horizontalComponents.getSecond().unApplyFilter();
        }
        for (T bsc : uncategorized) {
            bsc.unApplyFilter();
        }
    }
    
    public boolean canRemoveInstrumentResponse() {
        if (verticalComponent != null) {
            if (!verticalComponent.canRemoveInstrumentResponse()) {
                return false;
            }
        }
        if (horizontalComponents != null) {
            if (!horizontalComponents.getFirst().canRemoveInstrumentResponse()) {
                return false;
            }
            if (!horizontalComponents.getSecond().canRemoveInstrumentResponse()) {
                return false;
            }
        }
        for (T bsc : uncategorized) {
            if (!bsc.canRemoveInstrumentResponse()) {
                return false;
            }
        }
        return true;
    }
    
    public void printInventory() {
        System.out.println("ComponentSet===============================");
        if (verticalComponent != null) {
            System.out.println(verticalComponent.toString());
        }
        if (horizontalComponents != null) {
            System.out.println(horizontalComponents.getFirst().toString());
            System.out.println(horizontalComponents.getSecond().toString());
        }
        System.out.println("==================================================\n");
        
    }
    
    public ComponentSetPlot plotFor(ComponentSetPlotHolder holder) {
        return new ComponentSetPlot(this, holder);
    }
    
    public boolean isEmpty() {
        return verticalComponent == null && horizontalComponents == null && uncategorized.isEmpty();
    }
    
    public boolean isSaveRequired() {
        return arrivalManager.isSaveRequired();
    }
    
    @Override
    public int compareTo(ComponentSet other) {
        if (this == other) {
            return 0;
        }
        if (other == null) {
            return -1;
        }
        
        return this.getType().compareTo(other.getType());
    }
    
    private boolean canBeRotated(PairT<T, T> horizontalComponents) {
        T comp1 = horizontalComponents.getFirst();
        if (comp1.getRotationStatus() != RotationStatus.UNROTATED) {
            return false;
        }
        T comp2 = horizontalComponents.getSecond();
        if (comp2.getRotationStatus() != RotationStatus.UNROTATED) {
            return false;
        }
        double hang1 = comp1.getAzimuth();
        double hang2 = comp2.getAzimuth();
        double cosine = Math.cos((hang2 - hang1) * Math.PI / 180);
        return Math.abs(cosine) < ORTHOGONAL_COSINE_THRESHOLD;
    }
    
    private boolean uncategorizedAreDupliates(ComponentSet other) {
        if (uncategorized.size() != other.uncategorized.size()) {
            return false;
        } else {
            for (int j = 0; j < uncategorized.size(); ++j) {
                if (!uncategorized.get(j).getIdentifier().equals(other.uncategorized.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean verticalsAreDuplicates(ComponentSet other) {
        return ((verticalComponent == null && other.verticalComponent == null)
                || ((verticalComponent != null && other.verticalComponent != null)
                && verticalComponent.getIdentifier().equals(other.getVerticalComponent().getIdentifier())));
    }
    
    private boolean horizontalsAreDuplicates(ComponentSet<T> other) {
        if (horizontalComponents == null) {
            return other.horizontalComponents == null;
        } else {
            if (other.horizontalComponents != null) {
                T myComp1 = horizontalComponents.getFirst();
                T myComp2 = horizontalComponents.getSecond();
                T otherComp1 = other.horizontalComponents.getFirst();
                T otherComp2 = other.horizontalComponents.getSecond();
                return myComp1.getIdentifier().equals(otherComp1.getIdentifier()) && myComp2.getIdentifier().equals(otherComp2.getIdentifier());
            } else {
                return false;
            }
        }
    }
    
    public String getSta() {
        if (verticalComponent != null) {
            return verticalComponent.getSta();
        } else if (horizontalComponents != null) {
            return horizontalComponents.getFirst().getSta();
        } else if (uncategorized != null && !uncategorized.isEmpty()) {
            return uncategorized.get(0).getSta();
        } else {
            throw new IllegalStateException("Empty ComponentSet!");
        }
    }
    
    public String getBand() {
        if (verticalComponent != null) {
            return verticalComponent.getIdentifier().getBand();
        } else if (horizontalComponents != null) {
            return horizontalComponents.getFirst().getIdentifier().getBand();
        } else if (uncategorized != null && !uncategorized.isEmpty()) {
            return uncategorized.get(0).getIdentifier().getBand();
        } else {
            throw new IllegalStateException("Empty ComponentSet!");
        }
    }
    
    public String getInscode() {
        if (verticalComponent != null) {
            return verticalComponent.getIdentifier().getInstrument();
        } else if (horizontalComponents != null) {
            return horizontalComponents.getFirst().getIdentifier().getInstrument();
        } else if (uncategorized != null && !uncategorized.isEmpty()) {
            return uncategorized.get(0).getIdentifier().getInstrument();
        } else {
            throw new IllegalStateException("Empty ComponentSet!");
        }
    }
    
    public String getLocid() {
        if (verticalComponent != null) {
            return verticalComponent.getIdentifier().getLocid();
        } else if (horizontalComponents != null) {
            return horizontalComponents.getFirst().getIdentifier().getLocid();
        } else if (uncategorized != null && !uncategorized.isEmpty()) {
            return uncategorized.get(0).getIdentifier().getLocid();
        } else {
            throw new IllegalStateException("Empty ComponentSet!");
        }
    }
    
    private Collection<T> getComponentCollection() {
        Collection<T> result = new ArrayList<>();
        if (verticalComponent != null) {
            result.add(verticalComponent);
        }
        if (horizontalComponents != null) {
            result.add(horizontalComponents.getFirst());
            result.add(horizontalComponents.getSecond());
        }
        if (uncategorized != null && !uncategorized.isEmpty()) {
            result.addAll(uncategorized);
        }
        return result;
    }
    
    public int getValidArrivalCount() {
        return arrivalManager.getValidArrivalCount();
    }
    
    public boolean canCreatePicks() {
        return true;
    }
    
    public String maybeRemapPhase(String phase) {
        return phase;
    }
    
    private class ArrivalManager {
        
        private Map<DisplayArrival, ArrivalState> arrivals;
        private final Collection<ArrivalListener> arrivalListeners;
        
        public boolean isSaveRequired() {
            for (DisplayArrival arrival : arrivals.keySet()) {
                // read from db and changed it
                if (arrival.hasArid() && arrival.isDirty()) {
                    return true;
                    // read from db and deleted it
                } else if (arrival.hasArid() && arrivals.get(arrival) != null && arrivals.get(arrival).isDeleted()) {
                    return true;
                    // created new and did not want to keep it
                } else if (!arrival.hasArid() && arrivals.get(arrival) != null && !arrivals.get(arrival).isDeleted()) {
                    return true;
                }
            }
            return false;
        }
        
        public void removeDeletedArrivals() {
            printContents();
            Iterator<Map.Entry<DisplayArrival, ArrivalState>> iter = arrivals.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<DisplayArrival, ArrivalState> entry = iter.next();
                if (entry.getValue().isDeleted()) {
                    iter.remove();
                }
            }
            printContents();
        }
        
        public ArrivalManager() {
            arrivals = new HashMap<>();
            arrivalListeners = new ArrayList<>();
        }
        
        public void printContents() {
            ApplicationLogger.getInstance().log(Level.FINE, "Arrival Contents");
            for (DisplayArrival arr : arrivals.keySet()) {
                ArrivalState state = arrivals.get(arr);
                String objectId = Integer.toHexString(System.identityHashCode(arr));
                if (state != null) {
                    ApplicationLogger.getInstance().log(Level.FINE, String.format("\t%s-->%s", objectId, (state.deleted ? "deleted" : "un-deleted")));
                } else {
                    ApplicationLogger.getInstance().log(Level.FINE, String.format("\t%s-->%s", objectId, "state is null"));
                }
            }
            ApplicationLogger.getInstance().log(Level.FINE, "-------End Contents-------\n");
        }
        
        public ArrivalManager(ArrivalManager other) {
            arrivals = new HashMap<>(other.arrivals);
            arrivalListeners = new ArrayList<>(other.arrivalListeners);
        }
        
        public void addArrivalListener(ArrivalListener listener) {
            arrivalListeners.add(listener);
        }
        
        public void removeArrivalListener(ArrivalListener listener) {
            arrivalListeners.remove(listener);
        }
        
        public void addArrival(T component, DisplayArrival arrival) {
            ApplicationLogger.getInstance().log(Level.FINE, String.format("ComponentSet::addArrival::ArrivalManager--> Adding arrival (%s) HC= %d to component(%s) HC = %d...",
                    arrival, arrival.hashCode(), component, component.hashCode()));
            if (!hasArrival(arrival)) {
                ApplicationLogger.getInstance().log(Level.FINE, String.format("\tComponentSet::addArrival::ArrivalManager-->Arrival not found in collection of size(%d). Adding...",
                        arrivals.size()));
                
                arrivals.put(arrival, new ArrivalState(arrival, component));
                ApplicationLogger.getInstance().log(Level.FINE, String.format("\tComponentSet::addArrival::ArrivalManager-->After adding, arrivals size = %d", arrivals.size()));
                notifyArrivalOnComponent(component, arrival);
            } else if (arrivals.get(arrival).isDeleted()) {
                ApplicationLogger.getInstance().log(Level.FINE, String.format("\tComponentSet::addArrival::ArrivalManager-->The new arrival is marked as deleted"));
                ArrivalState arrivalState = arrivals.get(arrival);
                arrivalState.restore(component, arrival);
                ApplicationLogger.getInstance().log(Level.FINE, String.format("\tArrivalManager-->After restoring, arrivals size = %d", arrivals.size()));
                notifyArrivalOnComponent(arrivalState.getComponent(), arrivalState.getArrival());
                ApplicationLogger.getInstance().log(Level.FINE, "ComponentSet::addArrival::ArrivalManager-->Done adding arrival.\n");
            }
            printContents();
        }
        
        public T removeArrival(DisplayArrival arrival) {
            ApplicationLogger.getInstance().log(Level.FINE, String.format("ComponentSet::removeArrival-->Removing arrival (%s)...",
                    arrival));
            T selected = null;
            if (hasArrival(arrival) && arrival.isCanBeDeleted()) {
                selected = arrivals.get(arrival).getComponent();
                arrivals.get(arrival).setDeleted();
                for (ArrivalListener listener : arrivalListeners) {
                    listener.arrivalRemoved(arrival);
                }
            } else {
                ApplicationLogger.getInstance().log(Level.FINE, "\tComponentSet::removeArrival-->Arrival not found or could not be deleted!");
            }
            ApplicationLogger.getInstance().log(Level.FINE, "ComponentSet::removeArrival-->Done removing arrival.\n");
            printContents();
            return selected;
        }
        
        public boolean hasArrival(DisplayArrival arrival) {
            return arrivals.containsKey(arrival);
        }
        
        public int getValidArrivalCount() {
            int count = 0;
            for (DisplayArrival arrival : arrivals.keySet()) {
                ArrivalState as = arrivals.get(arrival);
                if (as != null && !as.isDeleted()) {
                    ++count;
                }
            }
            return count;
        }
        
        public Collection<DisplayArrival> getArrivals() {
            Collection<DisplayArrival> result = new ArrayList<>();
            for (DisplayArrival arrival : arrivals.keySet()) {
                ArrivalState as = arrivals.get(arrival);
                if (as != null && !as.isDeleted()) {
                    result.add(arrival);
                }
            }
            return result;
        }
        
        public boolean hasArrival(T component, String phase) {
            for (DisplayArrival arrival : arrivals.keySet()) {
                T selected = arrivals.get(arrival).getComponent();
                if (selected != null && selected == component) {
                    if (!arrivals.get(arrival).isDeleted() && arrival.getPhase().equals(phase)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        private void notifyArrivalOnComponent(T component, DisplayArrival arrival) {
            for (ArrivalListener listener : arrivalListeners) {
                listener.arrivalAdded(component, arrival);
            }
        }
    }
    
    private class ArrivalState {
        
        private final DisplayArrival arrival;
        private T component;
        private boolean deleted;
        
        public ArrivalState(DisplayArrival arrival, T component) {
            this.arrival = arrival;
            this.component = component;
            deleted = false;
        }
        
        public DisplayArrival getArrival() {
            return arrival;
        }
        
        public T getComponent() {
            return component;
        }
        
        public void restore(T component, DisplayArrival update) {
            this.component = component;
            arrival.setTime(update.getTime());
            deleted = false;
        }
        
        public void setDeleted() {
            deleted = true;
        }
        
        public boolean isDeleted() {
            return deleted;
        }
    }
}
