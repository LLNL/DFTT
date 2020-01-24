/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.classify;

import java.awt.Color;

/**
 *
 * @author dodge1
 */
public enum TriggerClassification {
    UNSET(Color.BLUE), ARTIFACT(Color.RED), UNUSABLE(Color.GRAY), GOOD(Color.GREEN), INCOMPLETE(Color.CYAN);

    public static TriggerClassification createFromStatusStrings(String artifactStatus, String usabilityStatus) {
        if (artifactStatus.equals("unset") && usabilityStatus.equals("unset")) {
            return TriggerClassification.UNSET;
        } else if (artifactStatus.equals("valid") && usabilityStatus.equals("valid")) {
            return TriggerClassification.GOOD;
        } else if (artifactStatus.equals("valid") && usabilityStatus.equals("invalid")) {
            return TriggerClassification.UNUSABLE;
        } else if (artifactStatus.equals("invalid")) {
            return TriggerClassification.ARTIFACT;
        } else {
            return TriggerClassification.INCOMPLETE;
        }
    }

    public static TriggerClassification createFromSingleStatusString(String status) {
        switch (status) {
            case "b":
                return TriggerClassification.ARTIFACT;
            case "g":
                return TriggerClassification.GOOD;
            case "u":
                return TriggerClassification.UNUSABLE;
            default:
                return TriggerClassification.UNSET;
        }
    }
    private final Color traceDisplayColor;

    private TriggerClassification(Color color) {
        traceDisplayColor = color;
    }

    public Color getTraceDisplayColor() {
        return traceDisplayColor;
    }
}
