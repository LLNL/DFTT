package llnl.gnem.core.gui.util;

import java.util.List;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 *
 * @author addair1
 */
public class MultiSplitPane {
    public static JComponent createMultiSplitPane(List<JComponent> components, int height) {
        if (components.size() == 1) {
            return components.get(0);
        }

        int split = components.size() / 2;
        List<JComponent> list1 = components.subList(0, split);
        List<JComponent> list2 = components.subList(split, components.size());

        int splitHeight = height * list1.size() / components.size();
        JSplitPane newPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                createMultiSplitPane(list1, splitHeight),
                createMultiSplitPane(list2, height - splitHeight));
        newPane.setOneTouchExpandable(true);
        newPane.setDividerLocation(splitHeight);
        newPane.setResizeWeight(0.5);
        return newPane;
    }
}
