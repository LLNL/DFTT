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
package llnl.gnem.apps.detection.sdBuilder.stackViewer;

import java.util.prefs.Preferences;

/**
 *
 * @author dodge1
 */
public class FKWindowParams {

    private double fkWindowLength;
    private final Preferences prefs;
    private double windowStart;
    private boolean showFKWindow;

    private static class ParameterModelHolder {

        private static final FKWindowParams INSTANCE = new FKWindowParams();
    }

    private FKWindowParams() {
        prefs = Preferences.userNodeForPackage(this.getClass());
        fkWindowLength = prefs.getDouble("FK_WINDOW_LENGTH", 10.0);
        windowStart = 0;
        showFKWindow = prefs.getBoolean("SHOW_FK_WINDOW", true);
    }

    public static FKWindowParams getInstance() {
        return ParameterModelHolder.INSTANCE;
    }

    public boolean isShowFKWindow() {
        return showFKWindow;
    }

    public void setShowFKWindow(boolean value) {
        showFKWindow = value;
        prefs.putBoolean("SHOW_FK_WINDOW", showFKWindow);
    }

    public void setFkWindowLength(double value) {
        this.fkWindowLength = value;
        prefs.putDouble("FK_WINDOW_LENGTH", value);
    }

    public double getFkWindowLength() {
        return fkWindowLength;
    }

    public void adjustWindowStart(double deltaT) {
        windowStart += deltaT;
    }

    public double getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(double value) {
        windowStart = value;
    }
}
