package llnl.gnem.core.database.login;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */

@SuppressWarnings({ "NonThreadSafeLazyInitialization" })
public class DisplayAllServicesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static DisplayAllServicesAction ourInstance;

    public static DisplayAllServicesAction getInstance(Object owner) {
        if (ourInstance == null)
            ourInstance = new DisplayAllServicesAction(owner);
        return ourInstance;
    }

    private Map<String, DbServiceInfo> dbServices;

    private DisplayAllServicesAction(Object owner) {
        super("Connections", Utility.getIcon(owner, "miscIcons/database16.gif"));
        putValue(SHORT_DESCRIPTION, "List Database Connections");
        putValue(MNEMONIC_KEY, KeyEvent.VK_O);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        ServiceDisplayPanel sdp = new ServiceDisplayPanel(dbServices);
        JOptionPane.showMessageDialog(null, sdp, "Database Services", JOptionPane.INFORMATION_MESSAGE, null);
    }

    public void setConnection(Map<String, DbServiceInfo> services) {
        this.dbServices = services;
    }
}