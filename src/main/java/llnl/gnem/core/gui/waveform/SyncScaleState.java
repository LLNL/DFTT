/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform;

import llnl.gnem.core.gui.util.StatefulAction;

public enum SyncScaleState implements StatefulAction.ActionState {

    OFF("Click to change to Synchronize Scale ON", "miscIcons/scaleEqual32.gif"), ON("Click to change to Synchronize Scale OFF", "miscIcons/scaleEqual32.gif");
    public final String description;

    public final String iconPath;

    private SyncScaleState(String description, String iconPath) {
        this.description = description;
        this.iconPath = iconPath;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getIconPath() {
        return this.iconPath;
    }

    public SyncScaleState getNext() {
        return values()[(ordinal() + 1) % values().length];
    }
}
