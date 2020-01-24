package llnl.gnem.core.gui.util;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;

/**
 * Created by dodge1
 * Date: Feb 12, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */


@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class ViewLogAction extends AbstractAction {

    private static ViewLogAction ourInstance;

    public static ViewLogAction getInstance(Object owner) {
        if (ourInstance == null)
            ourInstance = new ViewLogAction(owner);
        return ourInstance;
    }

    private ViewLogAction(Object owner) {
        super("Log", Utility.getIcon(owner, "miscIcons/document.gif"));
        putValue(SHORT_DESCRIPTION, "Click to view application log.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_L);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GuiHandler.getInstance().setVisible(true);
    }


}