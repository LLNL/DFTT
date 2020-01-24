package llnl.gnem.core.gui.map;

import llnl.gnem.core.gui.util.Utility;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * User: dodge1 Date: Mar 8, 2007 Time: 2:46:51 PM To
 * change this template use File | Settings | File Templates.
 */
public class GridLineDisplayAction extends AbstractAction {
    private static Map<Griddable, GridLineDisplayAction> instanceMap = new HashMap<Griddable, GridLineDisplayAction>();
    private ImageIcon showLinesIcon;
    private ImageIcon hideLinesIcon;
    private Griddable map;

    private GridLineDisplayAction(Griddable map) {
        super("", Utility.getIcon(map, "miscIcons/worldGrid32.gif"));
        this.map = map;
        showLinesIcon = Utility.getIcon(map, "miscIcons/worldGrid32.gif");
        hideLinesIcon = Utility.getIcon(map, "miscIcons/world32.gif");
        putValue(NAME, "Show Gridlines");
        putValue(SHORT_DESCRIPTION, "Show Latitude and Longitude lines on map.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_G);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (map.isGridVisible()) {
            putValue(SHORT_DESCRIPTION, "Show Latitude and Longitude lines on map.");
            putValue(SMALL_ICON, showLinesIcon);
        } else {
            putValue(SHORT_DESCRIPTION, "Do not display Latitude and Longitude lines on map.");
            putValue(SMALL_ICON, hideLinesIcon);
        }
        map.setGridVisible(!map.isGridVisible());
    }
    
    public static GridLineDisplayAction getInstance(Griddable map) {
        GridLineDisplayAction instance = instanceMap.get(map);
        if (instance == null) {
            instance = new GridLineDisplayAction(map);
            instanceMap.put(map, instance);
        }
        return instance;
    }
}
