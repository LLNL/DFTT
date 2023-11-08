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
package llnl.gnem.dftt.core.database.login;

import llnl.gnem.dftt.core.gui.util.SpringUtilities;

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