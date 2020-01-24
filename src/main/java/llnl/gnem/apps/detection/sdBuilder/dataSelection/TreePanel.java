/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import llnl.gnem.apps.detection.core.dataObjects.Detection;
import llnl.gnem.apps.detection.core.dataObjects.DetectorType;
import llnl.gnem.apps.detection.sdBuilder.actions.AdvanceAction;
import llnl.gnem.apps.detection.sdBuilder.actions.ClassifyAllDetectionsAction;
import llnl.gnem.apps.detection.sdBuilder.actions.CreateTemplateAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.apps.detection.sdBuilder.actions.DeleteConfigurationAction;
import llnl.gnem.apps.detection.sdBuilder.actions.DeleteDetectionAction;
import llnl.gnem.apps.detection.sdBuilder.actions.DeleteDetectorAction;
import llnl.gnem.apps.detection.sdBuilder.actions.DeleteRunAction;
import llnl.gnem.apps.detection.sdBuilder.actions.DisplayDetectorHistogramAction;
import llnl.gnem.apps.detection.sdBuilder.actions.DisplayDetectorTemplateAction;
import llnl.gnem.apps.detection.sdBuilder.actions.PopulateCorrelatorAction;
import llnl.gnem.apps.detection.sdBuilder.actions.SortByConfigNameAction;
import llnl.gnem.apps.detection.sdBuilder.actions.SortByConfigidAction;
import llnl.gnem.apps.detection.sdBuilder.actions.SortByDetectionCountAction;
import llnl.gnem.apps.detection.sdBuilder.actions.SortByDetectoridAction;
import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.picking.DetectionPhasePickModel;
import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.GetProjectionsAction;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ComputeCorrelationsWorker;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.WindowAdjustmentDirection;
import llnl.gnem.apps.detection.util.Configuration;
import llnl.gnem.apps.detection.util.FrameworkRun;
import llnl.gnem.core.gui.plotting.MouseMode;

/**
 *
 * @author dodge1
 */
public class TreePanel extends JPanel implements TreeSelectionListener {

    private static final long serialVersionUID = -4223515997419181528L;

    private final JTree tree;
    private final DefaultMutableTreeNode top;
    private final DefaultTreeModel treeModel;
    private DefaultMutableTreeNode currentSelection = null;

    private final Map<Integer, DefaultMutableTreeNode> detectionidTreeNodeMap;

    public TreePanel() {
        super(new BorderLayout());
        top = new DefaultMutableTreeNode("Configurations");
        treeModel = new DefaultTreeModel(top);
        detectionidTreeNodeMap = new HashMap<>();

        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        tree.addTreeSelectionListener(this);
        tree.addMouseListener(new MyMouseListener());

        tree.addKeyListener(new TreeKeyListener());

        KeyboardFocusManager mgr = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        mgr.addKeyEventDispatcher(new MyKeyEventDispatcher());

        JScrollPane treeView = new JScrollPane(tree);
        add(treeView, BorderLayout.CENTER);
    }

    private class MyKeyEventDispatcher implements KeyEventDispatcher {

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                ClusterBuilderFrame.getInstance().setMouseMode(MouseMode.SELECT_ZOOM);
                if (e.getKeyChar() == KeyEvent.VK_TAB) {
                    TreePanel.this.processTabKey();
                    return true;
                } else if (e.getKeyChar() == KeyEvent.VK_DELETE) {
                    deleteObject();
                    return true;
                }
            } else if (e.getID() == KeyEvent.KEY_PRESSED) {
                switch (e.getKeyChar()) {
                    case 'p':
                    case 'P':
                        DetectionPhasePickModel.getInstance().setCurrentPhase("P");
                        ClusterBuilderFrame.getInstance().setMouseMode(MouseMode.CREATE_PICK);
                        break;
                    case 's':
                    case 'S':
                        DetectionPhasePickModel.getInstance().setCurrentPhase("S");
                        ClusterBuilderFrame.getInstance().setMouseMode(MouseMode.CREATE_PICK);
                        break;
                }
                if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    CorrelatedTracesModel.getInstance().savePicks();
                }

            }
            switch (e.getKeyCode()) {
                case 37: {
                    CorrelatedTracesModel.getInstance().shiftWindowStart(WindowAdjustmentDirection.LEFT);
                    return true;
                }
                case 38: {
                    CorrelatedTracesModel.getInstance().changeWindowSize(WindowAdjustmentDirection.RIGHT);
                    return true;
                }
                case 39: {
                    CorrelatedTracesModel.getInstance().shiftWindowStart(WindowAdjustmentDirection.RIGHT);
                    return true;
                }
                case 40: {
                    CorrelatedTracesModel.getInstance().changeWindowSize(WindowAdjustmentDirection.LEFT);
                    return true;
                }
                default:
                    return false;

            }
        }

    }

    private void createNewDetector() {
        CreateTemplateAction.getInstance(null).actionPerformed(null);
        tree.requestFocusInWindow();
    }

    void buildTree(Collection<Configuration> data) {
        detectionidTreeNodeMap.clear();
        data.stream().map((config) -> new DefaultMutableTreeNode(config)).forEach((node) -> {
            top.add(node);
        });
        repaint();
    }

    @Override
    public void valueChanged(TreeSelectionEvent tse) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node == null) //Nothing is selected.     
        {
            return;
        }
        currentSelection = node;
        if (node.isLeaf()) {
            Object nodeInfo = node.getUserObject();
            if (nodeInfo instanceof Configuration) {

                Configuration configuration = (Configuration) nodeInfo;
                int configid = configuration.getConfigid();
                new GetRunCollectionWorker(configid, node).execute();

            } else if (nodeInfo instanceof FrameworkRun) {
                FrameworkRun frameworkRun = (FrameworkRun) nodeInfo;
                int runid = frameworkRun.getRunid();
                new GetDetectorStatsWorker(runid, node).execute();
            } else if (nodeInfo instanceof DetectorStats) {
                DetectorStats stats = (DetectorStats) nodeInfo;
                int runid = stats.getRunid();
                int detectorid = stats.getDetectorid();
                new GetDetectionsWorker(runid, detectorid, node).execute();
            }
        }
    }

    public void setRunCollection(Collection<FrameworkRun> result, DefaultMutableTreeNode targetNode) {
        for (FrameworkRun fr : result) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fr);
            treeModel.insertNodeInto(node, targetNode,
                    targetNode.getChildCount());

        }
        repaint();
    }

    public void setDetectorStats(Collection<DetectorStats> result, DefaultMutableTreeNode targetNode) {
        for (DetectorStats ds : result) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(ds);
            treeModel.insertNodeInto(node, targetNode,
                    targetNode.getChildCount());
        }
        repaint();
    }

    public void setDetections(Collection<ClassifiedDetection> result, DefaultMutableTreeNode targetNode) {
        for (Detection detection : result) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(detection);
            detectionidTreeNodeMap.put(detection.getDetectionid(), node);
            treeModel.insertNodeInto(node, targetNode,
                    targetNode.getChildCount());
        }
        repaint();
    }

    public void removeMultipleDetections(Collection<Integer> detectionidValues) {
        for (int detectionid : detectionidValues) {
            DefaultMutableTreeNode node = detectionidTreeNodeMap.get(detectionid);

            if (node != null) {
                updateDetectionCount(node);
                treeModel.removeNodeFromParent(node);
            }
            detectionidTreeNodeMap.remove(detectionid);
        }
        repaint();
    }

    private void updateDetectionCount(DefaultMutableTreeNode node) {
        TreeNode pnode = node.getParent();
        if (pnode instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) pnode;
            Object object = parent.getUserObject();
            if (object instanceof DetectorStats) {
                DetectorStats stats = (DetectorStats) object;
                stats.decrementDetectionCount();
            }
        }
    }

    void removeNode(DefaultMutableTreeNode node) {
        if (node.getUserObject() instanceof ClassifiedDetection) {
            ClassifiedDetection cd = (ClassifiedDetection) node.getUserObject();
            detectionidTreeNodeMap.remove(cd.getDetectionid());
            updateDetectionCount(node);
        }
        treeModel.removeNodeFromParent(node);
        repaint();
    }

    public void clearTree() {
        detectionidTreeNodeMap.clear();
        top.removeAllChildren();
        treeModel.reload();
    }

    public void setSelectedDetection(int detectionid) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

        printNode(root, detectionid);
        tree.requestFocusInWindow();
    }

    public void printNode(DefaultMutableTreeNode node, int detectionid) {

        int childCount = node.getChildCount();

        for (int i = 0; i < childCount; i++) {

            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            if (childNode.getChildCount() > 0) {
                printNode(childNode, detectionid);
            } else {
                if (childNode.getUserObject() instanceof Detection) {
                    Detection ds = (Detection) childNode.getUserObject();
                    if (ds.getDetectionid() == detectionid) {
                        tree.setSelectionPath(getPath(childNode));
                    }
                    tree.collapsePath(getPath(node));
                }
            }

        }

    }

    public static TreePath getPath(TreeNode treeNode) {
        List<Object> nodes = new ArrayList<>();
        if (treeNode != null) {
            nodes.add(treeNode);
            treeNode = treeNode.getParent();
            while (treeNode != null) {
                nodes.add(0, treeNode);
                treeNode = treeNode.getParent();
            }
        }

        return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
    }

    public void loadDetections() {
        if (currentSelection != null) {
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) currentSelection.getParent();
            if (parentNode.getUserObject() instanceof FrameworkRun) {
                FrameworkRun runInfo = (FrameworkRun) parentNode.getUserObject();
                Object userObject = currentSelection.getUserObject();
                if (userObject instanceof DetectorStats) {
                    DetectorStats stats = (DetectorStats) userObject;
                    int runid = stats.getRunid();
                    int detectorid = stats.getDetectorid();
                    PopulateCorrelatorAction action = PopulateCorrelatorAction.getInstance(this);
                    action.setRunInfo(runInfo);
                    action.setRunid(runid);
                    action.setDetectorid(detectorid);
                    action.actionPerformed(null);
                }
            }
        }
        tree.requestFocusInWindow();
    }

    private class TreeKeyListener extends KeyAdapter {

        public void KeyPressed(KeyEvent e) {
            switch (e.getKeyChar()) {
                case 'p':
                case 'P':
                    System.out.println("P");
                    DetectionPhasePickModel.getInstance().setCurrentPhase("P");
                    ClusterBuilderFrame.getInstance().setMouseMode(MouseMode.CREATE_PICK);
                    break;
                case 's':
                case 'S':
                    System.out.println("S");
                    DetectionPhasePickModel.getInstance().setCurrentPhase("S");
                    ClusterBuilderFrame.getInstance().setMouseMode(MouseMode.CREATE_PICK);
                    break;
            }

        }

        @Override
        public void keyReleased(KeyEvent event) {
            switch (event.getKeyChar()) {
                case KeyEvent.VK_DELETE:
                    deleteObject();
                    break;
                case KeyEvent.VK_ENTER:
                    loadDetections();
                    break;
                case 't':
                case 'T':
                    if (currentSelection != null) {
                        Object userObject = currentSelection.getUserObject();
                        if (userObject instanceof DetectorStats) {
                            DetectorStats stats = (DetectorStats) userObject;
                            int detectorid = stats.getDetectorid();
                            DisplayDetectorTemplateAction ddta = DisplayDetectorTemplateAction.getInstance(this);
                            ddta.setDetectorid(detectorid);
                            ddta.actionPerformed(null);
                            ClusterBuilderFrame.getInstance().toFront();
                            tree.requestFocusInWindow();
                        }
                    }
                    break;
                case 'q':
                case 'Q':
                    if (currentSelection != null) {
                        Object userObject = currentSelection.getUserObject();
                        if (userObject instanceof DetectorStats) {
                            DefaultMutableTreeNode previous = currentSelection.getPreviousSibling();
                            displayNode(previous);
                        }
                    }
                    break;
                case 'f':
                case 'F':
                    ClusterBuilderFrame.getInstance().applyCurrentFilter();
                    tree.requestFocusInWindow();
                    break;
                case 'b':
                    classifyAndNext("b", false);
                    break;
                case 'B':
                    classifyAndNext("b", true);
                    break;
                case 'g':
                    classifyAndNext("g", false);
                    break;
                case 'G':
                    classifyAndNext("g", true);
                    break;
                case 'u':
                    classifyAndNext("u", false);
                    break;
                case 'U':
                    classifyAndNext("u", true);
                    break;
                case 'c':
                case 'C':
                    correlateCurrent();
                    break;
                case 'd':
                case 'D':
                    createNewDetector();
                    break;
                default:
                    break;
            }
        }

        private void classifyAndNext(String status, boolean proceedToNext) {
            if (currentSelection != null) {
                Object userObject = currentSelection.getUserObject();
                if (userObject instanceof DetectorStats) {
                    DetectorStats stats = (DetectorStats) userObject;
                    int detectorid = stats.getDetectorid();
                    new ClassifyDetectionWorker(detectorid, status).execute();
                    if (!status.equals("g")) {
                        DefaultMutableTreeNode next = currentSelection.getNextSibling();
                        removeNode(currentSelection);
                        if (next != null && next.getUserObject() != null && next.getUserObject() instanceof DetectorStats) {
                            TreePath path = new TreePath(next.getPath());
                            tree.setSelectionPath(path);
                            tree.scrollPathToVisible(path);
                            if (proceedToNext) {
                                displayNode(next);
                            }
                        }
                    } else if (proceedToNext) {
                        DefaultMutableTreeNode next = currentSelection.getNextSibling();
                        displayNode(next);
                    }
                    tree.requestFocusInWindow();
                }
            }
        }

    }

    private void correlateCurrent() {
        ComputeCorrelationsWorker worker = new ComputeCorrelationsWorker(ParameterModel.getInstance().isFixShiftsToZero());
        worker.execute();
        tree.requestFocusInWindow();
    }

    private void deleteObject() {
        if (currentSelection != null) {
            Object userObject = currentSelection.getUserObject();
            if (userObject instanceof DetectorStats) {
                DefaultMutableTreeNode next = currentSelection.getNextSibling();
                DetectorStats stats = (DetectorStats) userObject;
                int detectorid = stats.getDetectorid();
                DeleteDetectorAction dda = DeleteDetectorAction.getInstance(this);
                dda.setDetectorid(detectorid);
                dda.setNode(currentSelection);
                dda.setPromptForDelete(false);
                dda.actionPerformed(null);
                if (next != null && next.getUserObject() != null && next.getUserObject() instanceof DetectorStats) {
                    TreePath path = new TreePath(next.getPath());
                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                    boolean capsLockIsOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
                    if (capsLockIsOn) {
                        stats = (DetectorStats) next.getUserObject();
                        int runid = stats.getRunid();
                        detectorid = stats.getDetectorid();
                        PopulateCorrelatorAction action = PopulateCorrelatorAction.getInstance(this);
                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) next.getParent();
                        FrameworkRun runInfo = (FrameworkRun) parentNode.getUserObject();
                        action.setRunInfo(runInfo);
                        action.setRunid(runid);
                        action.setDetectorid(detectorid);
                        action.actionPerformed(null);
                    }
                }
            } else if (userObject instanceof Detection) {
                Detection detection = (Detection) userObject;
                DeleteDetectionAction dda = DeleteDetectionAction.getInstance(this);
                dda.setDetection(detection);
                dda.setNode(currentSelection);
                DefaultMutableTreeNode next = currentSelection.getNextSibling();
                dda.actionPerformed(null);
                CorrelatedTracesModel.getInstance().detectionWasDeleted(detection.getDetectionid());
                if (next != null && next.getUserObject() != null && next.getUserObject() instanceof Detection) {
                    TreePath path = new TreePath(next.getPath());
                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                    Detection det = (Detection) next.getUserObject();
                    CorrelatedTracesModel.getInstance().setSelectedDetection(det);
                }
            }
        }
        tree.requestFocusInWindow();
    }

    private void processTabKey() {
        if (currentSelection != null) {
            Object userObject = currentSelection.getUserObject();
            if (userObject instanceof DetectorStats) {
                DefaultMutableTreeNode next = currentSelection.getNextSibling();
                displayNode(next);
            }
        }
        tree.requestFocusInWindow();
    }

    private void displayNode(DefaultMutableTreeNode aNode) {
        Object userObject;
        if (aNode != null && aNode.getUserObject() != null && aNode.getUserObject() instanceof DetectorStats) {
            userObject = aNode.getUserObject();
            TreePath path = new TreePath(aNode.getPath());
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) aNode.getParent();
            FrameworkRun runInfo = (FrameworkRun) parentNode.getUserObject();
            DetectorStats stats = (DetectorStats) userObject;
            int runid = stats.getRunid();
            int detectorid = stats.getDetectorid();
            PopulateCorrelatorAction action = PopulateCorrelatorAction.getInstance(this);
            action.setRunInfo(runInfo);
            action.setRunid(runid);
            action.setDetectorid(detectorid);
            action.actionPerformed(null);

        }
        tree.requestFocusInWindow();
    }

    private class MyMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent me) {
            potentiallyShowPopup(me);
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            potentiallyShowPopup(me);
        }
    }

    private void potentiallyShowPopup(MouseEvent me) {

        TreePath selPath = tree.getPathForLocation(me.getX(), me.getY());
        if (selPath != null) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            Object obj = tn.getUserObject();
            if (obj != null && obj instanceof Detection) {
                Detection det = (Detection) obj;
                CorrelatedTracesModel.getInstance().setSelectedDetection(det);
            }
            if (me.isPopupTrigger()) {

                tree.getSelectionModel().addSelectionPath(selPath);
                JPopupMenu popupMenu = buildPopup(selPath);
                // Only show popup if we have any menu items in it
                if (popupMenu.getComponentCount() > 0) {
                    popupMenu.show((Component) me.getSource(), me.getX(), me.getY());
                }
            }
        }
    }

    private JPopupMenu buildPopup(TreePath path) {
        JPopupMenu pm = new JPopupMenu();
        // Based on selection context, install actions...
        Object obj = path.getLastPathComponent();
        if (obj instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
            Object userObject = node.getUserObject();
            if (node == treeModel.getRoot()) {
                SortByConfigidAction action = SortByConfigidAction.getInstance(this);
                action.setNode(node);
                action.setTreeModel(treeModel);
                JMenuItem item = new JMenuItem(action);
                pm.add(item);

                SortByConfigNameAction scna = SortByConfigNameAction.getInstance(this);
                scna.setNode(node);
                scna.setTreeModel(treeModel);
                item = new JMenuItem(scna);
                pm.add(item);
            } else if (userObject instanceof Configuration) {
                Configuration cfg = (Configuration) userObject;
                int configid = cfg.getConfigid();
                DeleteConfigurationAction action = DeleteConfigurationAction.getInstance(this);
                action.setConfigid(configid);
                action.setNode(node);
                JMenuItem item = new JMenuItem(action);
                pm.add(item);

            } else if (userObject instanceof DetectorStats) {
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
                FrameworkRun runInfo = (FrameworkRun) parentNode.getUserObject();
                DetectorStats stats = (DetectorStats) userObject;
                int runid = stats.getRunid();
                GetProjectionsAction.getInstance(this).setRunid(runid);

                AdvanceAction.getInstance(this).setEnabled(CorrelatedTracesModel.getInstance().isCanAdvance());
                int detectorid = stats.getDetectorid();
                DetectorType type = stats.getDetectorType();
                PopulateCorrelatorAction action = PopulateCorrelatorAction.getInstance(this);
                action.setRunInfo(runInfo);
                action.setRunid(runid);
                action.setDetectorid(detectorid);
                JMenuItem item = new JMenuItem(action);
                pm.add(item);
                if (type == DetectorType.SUBSPACE) {
                    DisplayDetectorHistogramAction ddhAction = DisplayDetectorHistogramAction.getInstance(this);
                    ddhAction.setRunid(runid);
                    ddhAction.setDetectorid(detectorid);
                    item = new JMenuItem(ddhAction);
                    pm.add(item);
                }

                DeleteDetectorAction dda = DeleteDetectorAction.getInstance(this);
                dda.setDetectorid(detectorid);
                dda.setNode(node);
                dda.setPromptForDelete(true);
                item = new JMenuItem(dda);
                pm.add(item);

                DisplayDetectorTemplateAction ddta = DisplayDetectorTemplateAction.getInstance(this);
                ddta.setDetectorid(detectorid);
                item = new JMenuItem(ddta);
                pm.add(item);

            } else if (userObject instanceof FrameworkRun) {
                FrameworkRun frameworkRun = (FrameworkRun) userObject;
                int runid = frameworkRun.getRunid();
                SortByDetectoridAction sdAction = SortByDetectoridAction.getInstance(this);
                sdAction.setRunid(runid);
                sdAction.setNode(node);
                sdAction.setTreeModel(treeModel);
                JMenuItem item = new JMenuItem(sdAction);
                pm.add(item);

                SortByDetectionCountAction sdca = SortByDetectionCountAction.getInstance(this);
                sdca.setRunid(runid);
                sdca.setNode(node);
                sdca.setTreeModel(treeModel);
                item = new JMenuItem(sdca);
                pm.add(item);

                DeleteRunAction action = DeleteRunAction.getInstance(this);
                action.setRunid(runid);
                action.setNode(node);

                item = new JMenuItem(action);
                pm.add(item);

                ClassifyAllDetectionsAction cada = ClassifyAllDetectionsAction.getInstance(this);
                cada.setRunid(runid);
                item = new JMenuItem(cada);
                pm.add(item);

            } else if (userObject instanceof Detection) {
                Detection detection = (Detection) userObject;
                DeleteDetectionAction dda = DeleteDetectionAction.getInstance(this);
                dda.setDetection(detection);
                dda.setNode(node);
                JMenuItem item = new JMenuItem(dda);
                pm.add(item);
            }

        }

        return pm;
    }

    public void returnFocusToTree() {
        tree.requestFocusInWindow();
    }
}
