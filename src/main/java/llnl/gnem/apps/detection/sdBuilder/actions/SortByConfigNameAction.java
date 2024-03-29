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
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import llnl.gnem.apps.detection.util.Configuration;

import llnl.gnem.dftt.core.gui.util.Utility;

/**
 * Created by dodge1 Date: Mar 22, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class SortByConfigNameAction extends AbstractAction {

    private static SortByConfigNameAction ourInstance;

    public static SortByConfigNameAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new SortByConfigNameAction(owner);
        }
        return ourInstance;
    }
    private DefaultMutableTreeNode node;
    private DefaultTreeModel treeModel;

    private SortByConfigNameAction(Object owner) {
        super("Sort By ConfigName", Utility.getIcon(owner, "miscIcons/pagedown32.gif"));
        putValue(SHORT_DESCRIPTION, "Sort Configurations by Configuration Name");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (node != null && treeModel != null && treeModel.getRoot() == node) {
            Map<String, DefaultMutableTreeNode> idNodeMap = new TreeMap<>();
            Enumeration children = node.children();
            if (children != null) {
                while (children.hasMoreElements()) {
                    TreeNode childNode = (TreeNode) children.nextElement();
                    if (childNode instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) childNode;
                        Object obj = dmtn.getUserObject();
                        if (obj instanceof Configuration) {
                            Configuration config = (Configuration) obj;
                            String name = config.getName();
                            idNodeMap.put(name, dmtn);
                        }
                    }
                }
            }
            node.removeAllChildren();
            for (String name : idNodeMap.keySet()) {
                DefaultMutableTreeNode aNode = idNodeMap.get(name);
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
