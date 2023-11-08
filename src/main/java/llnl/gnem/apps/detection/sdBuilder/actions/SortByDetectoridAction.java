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
package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import llnl.gnem.apps.detection.sdBuilder.dataSelection.DetectorStats;

import llnl.gnem.dftt.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class SortByDetectoridAction extends AbstractAction {

    private static SortByDetectoridAction ourInstance;
    private int runid;

    public static SortByDetectoridAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new SortByDetectoridAction(owner);
        }
        return ourInstance;
    }
    private DefaultMutableTreeNode node;
    private DefaultTreeModel treeModel;

    private SortByDetectoridAction(Object owner) {
        super("Sort By Count", Utility.getIcon(owner, "miscIcons/pageup32.gif"));
        putValue(SHORT_DESCRIPTION, "Sort Detectors by Detection Count");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
    }

    public void setRunid(int runid) {
        this.runid = runid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (runid > 0 && node != null && treeModel != null) {
            Map<Integer, ArrayList<DefaultMutableTreeNode>> idStatsMap = new TreeMap<>();
            Enumeration children = node.children();
            if (children != null) {
                while (children.hasMoreElements()) {
                    TreeNode childNode = (TreeNode) children.nextElement();
                    if (childNode instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) childNode;
                        Object obj = dmtn.getUserObject();
                        if (obj instanceof DetectorStats) {
                            DetectorStats ds = (DetectorStats) obj;
                            int key = -ds.getDetectionCount();
                            ArrayList<DefaultMutableTreeNode> myNodes = idStatsMap.get(key);
                            if (myNodes == null) {
                                myNodes = new ArrayList<>();
                                idStatsMap.put(key, myNodes);
                            }
                            myNodes.add(dmtn);
                        }
                    }
                }
            }
            node.removeAllChildren();
            idStatsMap.keySet().stream().map((id) -> idStatsMap.get(id)).forEach((dmtnList) -> {
                dmtnList.stream().forEach((dmtn) -> {
                    node.add(dmtn);
                });
            });
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
