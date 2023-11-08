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
package llnl.gnem.dftt.core.gui.map.internal;

import java.util.prefs.Preferences;

public class LayerPreferences {

    private static final LayerPreferences instance = new LayerPreferences();
    private final Preferences prefs;

    public static LayerPreferences getInstance() {
        return instance;
    }

    private LayerPreferences() {
        prefs = Preferences.userNodeForPackage(this.getClass());
    }

    public void defaultLayerEnabled(String layerName, boolean value) {
        prefs.putBoolean(layerName, value);
    }

    public boolean getLayerEnabled(String layerName) {
        return prefs.getBoolean(layerName, false);
    }

    public void setLayerEnabled(String layerName, boolean value) {
        prefs.putBoolean(layerName, value);
    }

    public boolean containsLayer(String name) {
        return !prefs.get(name, "xxx").equals("xxx");
    }
}
