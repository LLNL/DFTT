/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author dodge1
 */
public class ProjectionModel {

    private final Collection<ProjectionView> views;
    private ProjectionCollection projectionCollection;

    public ProjectionCollection getProjectionCollection() {
        return projectionCollection;
    }

    public int getDetectorid()
    {
        return projectionCollection != null ? projectionCollection.getDetectorid() : -1;
    }

    private ProjectionModel() {
        views = new ArrayList<>();
    }

    public static ProjectionModel getInstance() {
        return ProjectionModelHolder.INSTANCE;
    }

    void setProjections(ProjectionCollection result) {
        projectionCollection = result;
        updateViewsForNewProjections();
        
    }

    private void updateViewsForNewProjections() {
        for( ProjectionView view : views){
            view.updateForNewProjection();
        }
    }

    private static class ProjectionModelHolder {

        private static final ProjectionModel INSTANCE = new ProjectionModel();
    }

    public void setView(ProjectionView view) {
        views.add(view);
    }

    public void clear() {
        projectionCollection = null;
         for( ProjectionView view : views){
            view.clear();
        }
    }
}
