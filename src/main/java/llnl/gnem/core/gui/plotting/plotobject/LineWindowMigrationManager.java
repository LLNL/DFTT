package llnl.gnem.core.gui.plotting.plotobject;

/**
 * User: dodge1
 * Date: Aug 1, 2005
 * Time: 9:41:09 AM
 * To change this template use File | Settings | File Templates.
 */
public interface LineWindowMigrationManager {

    void startTimeChanged( LineWindow window, double dt );

    void endTimeChanged( LineWindow window, double dt );

    void windowWasMoved( LineWindow window, double dt );

}
