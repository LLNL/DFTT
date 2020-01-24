package llnl.gnem.core.database.login;

import llnl.gnem.core.database.RoleManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by dodge1
 * Date: Feb 19, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class LoginAuxInfoPanel extends JPanel implements ActionListener {
    private JRadioButton loginasAdminChk;

    public boolean isAdminStatusSelected() {
        return loginasAdminChk != null &&  loginasAdminChk.isSelected();
    }

    public LoginAuxInfoPanel( RoleManager roleManager) {
       
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }
}
