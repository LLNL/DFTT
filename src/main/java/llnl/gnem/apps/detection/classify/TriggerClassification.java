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
