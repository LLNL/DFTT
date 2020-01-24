package llnl.gnem.apps.detection.sdBuilder;

import llnl.gnem.core.gui.filter.FilterGuiContainer;
import llnl.gnem.core.waveform.filter.FilterClient;

/**
 *
 * @author dodge1
 */
public class BuilderFilterContainer extends FilterGuiContainer {

    private static final long serialVersionUID = -6077188644167054963L;

    public BuilderFilterContainer(FilterClient client) {
        super("detection/sdBuilder",
                "Filters", null);
        getGui().addClient(client);
    }

}
