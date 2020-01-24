/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.database.login;

import llnl.gnem.core.gui.util.SpringUtilities;

import javax.swing.*;

/**
 * Created By: ganzberger1
 * Date: Jul 9, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2010 Lawrence Livermore National Laboratory.
 */

public class ServiceSpecPanel extends JPanel {


    private final JTextField serverNameField;
    private final JTextField portNameField;
    private final JTextField serviceNameField;

    public ServiceSpecPanel() {

        setLayout(new SpringLayout());

        JLabel label = new JLabel("Server Name", JLabel.TRAILING);
        add(label);
        serverNameField = new JTextField(20);
        label.setLabelFor(serverNameField);
        add(serverNameField);


        label = new JLabel("Port", JLabel.TRAILING);
        add(label);
        portNameField = new JTextField("1521");
        label.setLabelFor(portNameField);
        add(portNameField);


        label = new JLabel("Service Name", JLabel.TRAILING);
        add(label);
        serviceNameField = new JTextField();
        label.setLabelFor(serviceNameField);
        add(serviceNameField);
        SpringUtilities.makeCompactGrid(this,
                3, 2,
                5, 5, //initX, initY
                5, 5);

    }

    public String getServerName()
    {
        return serverNameField.getText();
    }

    public int getPort()
    {
        return Integer.parseInt(portNameField.getText().trim());
    }

    public String getServiceName()
    {
        return serviceNameField.getText();
    }

}