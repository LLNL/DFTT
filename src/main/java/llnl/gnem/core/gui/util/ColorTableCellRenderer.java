package llnl.gnem.core.gui.util;

import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import java.awt.*;

/**
 * ColorTableCellRenderer.java
 * From Core Java 2 Chapter 6
 *
 * User: Eric Matzel
 * Date: Sep 14, 2007
 */

/**
 * This Class is used to allow Color Objects which are included in Table GUI objects to be rendered as the color itself.
 * e.g. the cell would be drawn as a red pane instead of the identifier "java.awt.Color.Red"
 *
 * Usage: table.setDefaultRenderer(Color.class, new ColorTableCellRenderer());
 */
public class ColorTableCellRenderer implements TableCellRenderer
{
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        panel.setBackground((Color) value);
        return panel;
    }

    // The following panel is returned for all cells, with the background color set to the Color value of the cell

    private JPanel panel = new JPanel();
}