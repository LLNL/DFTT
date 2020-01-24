package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import llnl.gnem.apps.detection.util.Configuration;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class SortByConfigidAction extends AbstractAction {

    private static SortByConfigidAction ourInstance;

    public static SortByConfigidAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new SortByConfigidAction(owner);
        }
        return ourInstance;
    }
    private DefaultMutableTreeNode node;
    private DefaultTreeModel treeModel;

    private SortByConfigidAction(Object owner) {
        super("Sort By Configurationid", Utility.getIcon(owner, "miscIcons/pageup32.gif"));
        putValue(SHORT_DESCRIPTION, "Sort Configurations by Configurationid");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (node != null && treeModel != null && treeModel.getRoot() == node) {
            Map<Integer, DefaultMutableTreeNode> idNodeMap = new TreeMap<>();
            Enumeration children = node.children();
            if (children != null) {
                while (children.hasMoreElements()) {
                    TreeNode childNode = (TreeNode) children.nextElement();
                    if (childNode instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) childNode;
                        Object obj = dmtn.getUserObject();
                        if (obj instanceof Configuration) {
                            Configuration config = (Configuration) obj;
                            int configid = config.getConfigid();
                            idNodeMap.put(configid, dmtn);
                        }
                    }
                }
            }
            node.removeAllChildren();
            for (int configid : idNodeMap.keySet()) {
                DefaultMutableTreeNode aNode = idNodeMap.get(configid);
                node.add(aNode);
            }
            treeModel.nodeChanged(node);
            treeModel.reload(node);
        }

    }

    public void setNode(DefaultMutableTreeNode node) {
        this.node = node;
    }

    public void setTreeModel(DefaultTreeModel treeModel) {
        this.treeModel = treeModel;
    }

}