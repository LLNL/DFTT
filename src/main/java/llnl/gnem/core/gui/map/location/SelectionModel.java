package llnl.gnem.core.gui.map.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author addair1
 */
public class SelectionModel<T> {
    private final List<SelectionChangeListener<T>> listeners;
    private T current;
    
    public SelectionModel() {
        listeners = Collections.synchronizedList(new ArrayList<SelectionChangeListener<T>>());
        current = null;
    }
    
    public void setCurrent(T info) {
        if (info != null && info != current) {
            current = info;
            notifyViewsSelectionChanged();
        }
    }
        
    public void clearCurrent() {
        current = null;
        notifyViewsSelectionChanged();
    }
    
    public T getCurrent() {
        return current;
    }
    
    public void addChangeListener(SelectionChangeListener<T> listener) {
        listeners.add(listener);
    }
    
    protected void assign(T info) {
        current = info;
    }
    
    protected void notifyViewsSelectionCleared() {
        for (SelectionChangeListener listener : listeners) {
            listener.selectionChanged(null);
        }
    }
    
    protected void notifyViewsSelectionChanged() {
        for (SelectionChangeListener listener : listeners) {
            listener.selectionChanged(current);
        }
    }
}
