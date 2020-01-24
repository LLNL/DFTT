/**
 * User: Doug
 * Date: Mar 7, 2010
 * Time: 8:44:45 AM
 *  COPYRIGHT NOTICE
 *  Copyright (C) 2008 Doug Dodge.
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
