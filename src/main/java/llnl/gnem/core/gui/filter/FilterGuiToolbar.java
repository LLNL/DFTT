package llnl.gnem.core.gui.filter;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JToolBar;
import llnl.gnem.core.gui.filter.actions.*;

/**
 *
 * @author dodge1
 */
public class FilterGuiToolbar extends JToolBar {
    public FilterGuiToolbar(FilterGuiContainer owner) {
        super();

        JButton button = new JButton(AddCurrentAction.getInstance(this));
        addButton(button);

        button = new JButton(RemoveFilterAction.getInstance(this));
        addButton(button);
        this.addSeparator();


        button = new JButton(new ApplyFilterAction(owner));
        addButton(button);

        button = new JButton(new UnapplyFilterAction(owner));
        addButton(button);

        this.addSeparator();
        button = new JButton(new ExitAction(owner));
        addButton(button);
    }

    private void addButton(JButton button) {
        if (button.getIcon() != null) {
            button.setText(""); //an icon-only button
        }
        button.setPreferredSize(new Dimension(22, 22));
        button.setMaximumSize(new Dimension(22, 22));
        add(button);
    }

    public void setFilterGui(FilterGui gui) {
        AddCurrentAction.getInstance(this).setFilterGui(gui);
    }
}
