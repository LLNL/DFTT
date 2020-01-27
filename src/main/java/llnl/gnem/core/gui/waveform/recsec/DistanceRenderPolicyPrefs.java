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
package llnl.gnem.core.gui.waveform.recsec;

import java.util.prefs.Preferences;

public class DistanceRenderPolicyPrefs {

    private Preferences prefs;
    private DistanceRenderPolicy policy;
    private static DistanceRenderPolicyPrefs ourInstance = new DistanceRenderPolicyPrefs();

    public static DistanceRenderPolicyPrefs getInstance() {
        return ourInstance;
    }

    private DistanceRenderPolicyPrefs() {
        prefs = Preferences.userNodeForPackage(this.getClass());
        boolean orderBy = prefs.getBoolean("ORDER_BY_DISTANCE", true);
        policy = orderBy ? DistanceRenderPolicy.ORDER_BY_DISTANCE : DistanceRenderPolicy.PRESERVE_EXACT_DISTANCE;
    }

    public DistanceRenderPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(DistanceRenderPolicy policy) {
        boolean orderBy = policy == DistanceRenderPolicy.ORDER_BY_DISTANCE;
        prefs.putBoolean("ORDER_BY_DISTANCE", orderBy);
        this.policy = policy;
    }
}
