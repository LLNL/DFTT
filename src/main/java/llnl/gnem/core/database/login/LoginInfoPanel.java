package llnl.gnem.core.database.login;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import llnl.gnem.core.gui.util.SpringUtilities;

/**
 * Created by: dodge1
 * Date: Dec 8, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class LoginInfoPanel extends JPanel {
    private final JTextField nameField;
    private final JPasswordField password;
    private final JComboBox<String> serviceCombo;
    private final DbServiceInfoManager dsim;
    private final Map<String, DbServiceInfo> services;
    
    public LoginInfoPanel( DbServiceInfoManager dsim )
    {
        super( new SpringLayout() );
        this.dsim = dsim;
        int numPairs = 3;
        JLabel label = new JLabel( "User Name: ", JLabel.TRAILING );
        add( label );
        nameField = new JTextField( 10 );
        label.setLabelFor( nameField );
        add( nameField );
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(16,16));
        add(spacer);
        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(16,16));
        add(spacer);


        label = new JLabel( "Password: ", JLabel.TRAILING );
        add( label );
        password = new JPasswordField( 10 );
        label.setLabelFor( password );
        add( password );
        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(16,16));
        add(spacer);
        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(16,16));
        add(spacer);

        label = new JLabel( "Service: ", JLabel.TRAILING );
        add( label );
        
        services = new TreeMap<>();
        for( DbServiceInfo info : dsim.getServices()) {
            services.put(info.getServiceId(), info);
        }
        
        if(services.isEmpty()) {
            throw new RuntimeException("Database configuration is invalid or cannot be found!");
        }
        
        serviceCombo = new JComboBox<String>(services.keySet().toArray(new String[services.keySet().size()]));
        DbServiceInfo selected = dsim.getSelectedService();
        if (selected != null) {
            serviceCombo.setSelectedItem( selected.getServiceId() );
        }
        serviceCombo.setPreferredSize(new Dimension(200,20));
        label.setLabelFor( serviceCombo );
        add( serviceCombo );
        
        JButton listButton = new JButton(DisplayAllServicesAction.getInstance(this));
        DisplayAllServicesAction.getInstance(this).setConnection(services);
        listButton.setText("");
        listButton.setPreferredSize(new Dimension( 22,22));
        listButton.setToolTipText("List Database Service Connection Details");
        add(listButton);

        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(16,16));
        add(spacer);

        SpringUtilities.makeCompactGrid( this,
                                         numPairs, 4, //rows, cols
                                         6, 6, //initX, initY
                                         1, 6 );       //xPad, yPad

        setBorder( new CompoundBorder( new EtchedBorder( EtchedBorder.RAISED ), new EmptyBorder( 5, 5, 5, 5 ) ) );
    }

    public String getLoginName()
    {
        return nameField.getText();
    }

    public String getPassword()
    {
        return new String( password.getPassword() );
    }

    public DbServiceInfo getSelectedService() throws IOException {
        String key = (String)serviceCombo.getSelectedItem();
        DbServiceInfo info =  services.get(key);
        dsim.setSelectedService(info);
        return info;
    }

}
