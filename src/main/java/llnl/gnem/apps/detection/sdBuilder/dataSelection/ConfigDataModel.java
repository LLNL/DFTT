/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.dataSelection;

import java.util.Collection;
import javax.swing.tree.DefaultMutableTreeNode;
import llnl.gnem.apps.detection.core.dataObjects.Detection;
import llnl.gnem.apps.detection.util.Configuration;
import llnl.gnem.apps.detection.util.FrameworkRun;

/**
 *
 * @author dodge1
 */
public class ConfigDataModel {
    
    private TreePanel view;
    
    private ConfigDataModel() {
    }
    
    public static ConfigDataModel getInstance() {
        return ConfigDataModelHolder.INSTANCE;
    }

    public void setRunCollection(Collection<FrameworkRun> result, DefaultMutableTreeNode targetNode) {
        view.setRunCollection( result,  targetNode );
    }

    public void setDetectorStats(Collection<DetectorStats> result, DefaultMutableTreeNode targetNode) {
        view.setDetectorStats(result, targetNode);
    }

    public void setDetections(Collection<ClassifiedDetection> result, DefaultMutableTreeNode targetNode) {
        view.setDetections(result, targetNode);
    }

    void removeNode(DefaultMutableTreeNode node) {
        view.removeNode(node);
    }
    
    private static class ConfigDataModelHolder {

        private static final ConfigDataModel INSTANCE = new ConfigDataModel();
    }
    
    public void setView(TreePanel treePanel)
    {
        this.view = treePanel;
    }
    
    public void setConfigurationData( Collection<Configuration> data )
    {
        view.buildTree(data);
    }
}
