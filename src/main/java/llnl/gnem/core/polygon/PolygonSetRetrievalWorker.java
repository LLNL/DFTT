/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import llnl.gnem.core.dataAccess.DAOFactory;
import llnl.gnem.core.util.ApplicationLogger;

/**
 *
 * @author dodge1
 */
public class PolygonSetRetrievalWorker extends SwingWorker<Void, Void> {

    private final List<PolygonSet> polygonSets = new ArrayList<>();
    private final PolygonSetModel owner;

    public PolygonSetRetrievalWorker(PolygonSetModel owner) {
        this.owner = owner;
    }

    @Override
    protected Void doInBackground() throws Exception {

        Collection<PolygonSet> result = DAOFactory.getInstance().getPolygonDAO().getAllPolygonSets();
        polygonSets.addAll(result);
        return null;
    }

    @Override
    public void done() {

        try{
            get();
            owner.addAllSets(polygonSets);
        } catch (InterruptedException ex) {
            //
        } catch (ExecutionException ex) {
            ApplicationLogger.getInstance().log(Level.WARNING, "Failure retrieving polygon list.", ex);
        }
    }
}
