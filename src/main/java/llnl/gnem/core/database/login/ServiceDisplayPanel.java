package llnl.gnem.core.database.login;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Created by dodge1
 * Date: Jan 21, 2011
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class ServiceDisplayPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    private final JList<DbServiceInfo> list;
    private final DefaultListModel<String> model;
 
    public ServiceDisplayPanel(Map<String, DbServiceInfo> dbServices) {

        model = new DefaultListModel();
        for (DbServiceInfo info : dbServices.values()) {
            //TODO: strip extra spaces/line ends
            String display = info.toString().trim().replaceAll(" +", " ");
            model.addElement(display);
        }
        
        list = new JList(model);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        add(scrollPane);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
}
