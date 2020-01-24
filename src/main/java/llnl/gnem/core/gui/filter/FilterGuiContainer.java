/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.filter;

import java.awt.BorderLayout;
import java.awt.Image;
import llnl.gnem.core.gui.util.PersistentPositionContainer;

/**
 *
 * @author dodge1
 */
public class FilterGuiContainer extends PersistentPositionContainer {

    private static final long serialVersionUID = -7001958459643230775L;

    private final FilterGui gui;

    public FilterGuiContainer(String path, String title, Image icon) {
        super(path, title, 480, 310);

        setIconImage(icon);
        gui = new FilterGui(this);
        FilterModel.getInstance().addView(gui);

        FilterGuiToolbar toolbar = new FilterGuiToolbar(this);
        toolbar.setFilterGui(gui);
        getContentPane().add(toolbar, BorderLayout.NORTH);
        getContentPane().add(gui, BorderLayout.CENTER);
        this.setSize(480, 310);
        this.setResizable(false);
        updateCaption();
    }

    public FilterGui getGui() {
        return gui;
    }


    public FilterToolbarControl getFilterToolbarControl() {
        return gui.getFilterToolbarControl();
    }


}
