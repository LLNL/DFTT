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
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DetectorStats;

import llnl.gnem.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class SortByDetectionCountAction extends AbstractAction {

    private static SortByDetectionCountAction ourInstance;
    private int runid;

    public static SortByDetectionCountAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new SortByDetectionCountAction(owner);
        }
        return ourInstance;
    }
    private DefaultMutableTreeNode node;
    private DefaultTreeModel treeModel;

    private SortByDetectionCountAction(Object owner) {
        super("Sort By ID", Utility.getIcon(owner, "miscIcons/pagedown32.gif"));
        putValue(SHORT_DESCRIPTION, "Sort Detectors by Detector ID");
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
    }

    public void setRunid(int runid) {
        this.runid = runid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (runid > 0 && node != null && treeModel != null) {
            Map<Integer, DefaultMutableTreeNode> idStatsMap = new TreeMap<>();
            Enumeration children = node.children();
            if (children != null) {
                while (children.hasMoreElements()) {
                    TreeNode childNode = (TreeNode) children.nextElement();
                    if (childNode instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) childNode;
                        Object obj = dmtn.getUserObject();
                        if (obj instanceof DetectorStats) {
                            DetectorStats ds = (DetectorStats) obj;
                            idStatsMap.put(ds.getDetectorid(), dmtn);
                        }
                    }
                }
            }
            node.removeAllChildren();
            for (int id : idStatsMap.keySet()) {
                DefaultMutableTreeNode dmtn = idStatsMap.get(id);
                node.add(dmtn);
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
