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
