package llnl.gnem.core.gui.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Created by dodge1 Date: Mar 29, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class GuiHandler extends Handler implements ActionListener {

    private static final String DEFAULT_APP_LOG_TITLE = "Application Log";
    private final JFrame frame;
    private final JTextArea textArea;
    private String applicationName;
    private final JButton clearButton;
   
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == clearButton) {
            textArea.setText("");
        }
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    private static class GuiHandlerHolder {

        private static final GuiHandler instance = new GuiHandler();
    }

    public static GuiHandler getInstance() {
        return GuiHandlerHolder.instance;
    }

    private GuiHandler() {
        frame = new JFrame();
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        ImageIcon buttonIcon = Utility.getIcon(this, "miscIcons/remove32.gif");
        clearButton = new JButton("Clear", buttonIcon);
        clearButton.addActionListener(this);
        
        JPanel bottom = new JPanel();
        bottom.add(clearButton);
        frame.getContentPane().add(bottom, BorderLayout.SOUTH);
        
        frame.setSize(800, 600);
        frame.setTitle(DEFAULT_APP_LOG_TITLE);
        ImageIcon icon = Utility.getIcon(this, "miscIcons/file48.gif");
        frame.setIconImage(icon.getImage());
        Dimension dim = frame.getToolkit().getScreenSize();
        Rectangle abounds = frame.getBounds();
        frame.setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
        applicationName = "";

    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    @Override
    public void publish(LogRecord logRecord) {
        if (isLoggable(logRecord)) {
            textArea.append(getFormatter().format(logRecord));
        }
    }

    @Override
    public void flush() {
        // Nothing to do with this handler.
    }

    @Override
    public void close() throws SecurityException {
        // Nothing to do with this handler.
    }

}
