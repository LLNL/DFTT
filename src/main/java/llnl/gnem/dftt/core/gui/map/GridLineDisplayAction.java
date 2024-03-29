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
package llnl.gnem.dftt.core.gui.map;

import llnl.gnem.dftt.core.gui.util.Utility;

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
