package llnl.gnem.core.gui.waveform.factory;

import javax.swing.JTabbedPane;
import llnl.gnem.core.gui.map.EditableMap;
import llnl.gnem.core.gui.map.PrintableMap;
import llnl.gnem.core.gui.map.ViewPort;
import llnl.gnem.core.gui.map.origins.OriginViewingMap;
import llnl.gnem.core.gui.util.PersistentPositionContainer;

/**
 *
 * @author addair1
 */
public abstract class MainForm extends PersistentPositionContainer {
    public MainForm(String path, String title, int width, int height) {
        super(path, title, width, height);
    }

    public abstract JTabbedPane getTabbedPane();

    public abstract ViewPort getMapViewPort();

    public abstract PrintableMap getPrintableMap();

    public abstract EditableMap getEditableMap();

    public abstract OriginViewingMap getOriginViewingMap();
}
