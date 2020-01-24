package llnl.gnem.apps.detection.sdBuilder.templateDisplay;

import llnl.gnem.apps.detection.sdBuilder.templateDisplay.projections.GetProjectionsAction;
import llnl.gnem.apps.detection.sdBuilder.*;
import java.awt.*;
import javax.swing.*;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class TemplateDlgToolbar extends JToolBar {

    private static final long serialVersionUID = 5342681835228434853L;

    private final DimensionSelector selector;

    public TemplateDlgToolbar() {
        super();

        JButton button = new JButton(ExitAction.getInstance(this));
        addButton(button);

        button = new JButton(WriteTemplateAction.getInstance(this));
        addButton(button);
        button = new JButton(GetProjectionsAction.getInstance(this));
        addButton(button);
        selector = new DimensionSelector();
        add(selector);

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(500, 10));
        add(spacer);
    }

    private void addButton(JButton button) {
        if (button.getIcon() != null) {
            button.setText(""); //an icon-only button
        }
        button.setPreferredSize(new Dimension(38, 36));
        button.setMaximumSize(new Dimension(38, 36));
        add(button);
    }

    /**
     * @return the selector
     */
    public DimensionSelector getSelector() {
        return selector;
    }

}
