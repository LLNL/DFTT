package llnl.gnem.core.gui.util;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created by dodge1
 * Date: Feb 12, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */


/**
 * =========================================================================
 * Class to take actions upon exit of code.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class ExitAction extends AbstractAction {

    private static ExitAction ourInstance;

    /**
     * =========================================================================
     * Return an instance of this ExitAction class.
     *
     * @param owner - The Object that owns this instance.
     * @return ourInstance - Instance of this ExitAction class
     */
    public static ExitAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new ExitAction(owner);
        }
        return ourInstance;
    }


    /**
     * =========================================================================
     * Class constructor.
     *
     * @param owner - The Object that owns this instance.
     */
    private ExitAction(Object owner) {
        super("Exit", Utility.getIcon(owner, "miscIcons/exit32.gif"));
        putValue(SHORT_DESCRIPTION, "Click to exit program.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    }


    /**
     * =========================================================================
     * Do activities based on events.
     *
     * @param e - And ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            System.exit(0);
        } catch (Exception e1) {
            ExceptionDialog.displayError(e1);
        }
    }


}
