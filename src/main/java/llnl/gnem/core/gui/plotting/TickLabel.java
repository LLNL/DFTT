/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.plotting;

/**
 *
 * @author dodge
 */
public class TickLabel {

    private final String label1;
    private final String label2;

    public TickLabel() {
        label1 = null;
        label2 = null;
    }

    public TickLabel(String label) {
        label1 = label;
        label2 = null;
    }

    public TickLabel(String label1, String label2) {
        this.label1 = label1;
        this.label2 = label2;
    }

    public boolean hasLabel1() {
        return label1 != null && !label1.isEmpty();
    }

    public boolean hasLabel2() {
        return label2 != null && !label2.isEmpty();
    }

    public String getLabel1() {
        return label1;
    }

    public String getLabel2() {
        return label2;
    }
}
