package llnl.gnem.core.gui.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import llnl.gnem.core.dataAccess.dataObjects.ProgressMonitor;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;


public class ProgressDialogBase extends JFrame implements ProgressMonitor{

    private static final long serialVersionUID = 3572671965357759656L;
    protected final ProgressPanel panel;
    protected ArrayList< ProgressPanel> panels;
    protected JPanel scrollPaneContainer;
    protected JScrollPane scrollPane;

    public void setReferenceFrame(JFrame reference) {
        if( reference != null ) {
            Point point = reference.getLocationOnScreen();
            Dimension dim = reference.getSize();
            Dimension mySize = this.getSize();
            int myX = (int)(point.getX() + (dim.getWidth() - mySize.getWidth()) / 2);
            int myY = (int) (point.getY() + (dim.getHeight() - mySize.getHeight()) / 2);
            this.setLocation(myX, myY);
        }
    }

    @Override
    public void setText(String textString) {
        panel.setText(textString);
    }
    
    @Override
    public void setTitle( String text)
    {
        panel.setTitle(text);
    }

    public void setProgressStringPainted(boolean b) {
        panel.setProgressStringPainted(b);
    }
    
    @Override
    public void setProgressStateIndeterminate( boolean state ){
        setProgressBarIndeterminate(state);
    }

    public void setProgressBarIndeterminate(boolean b) {
        panel.setProgressBarIndeterminate(b);
    }

    @Override
    public void setRange( int minValue, int maxValue){
        setMinMax(minValue,maxValue);
    }
    public void setMinMax(int i, int count) {
        panel.setMinMax(i, count);
    }

    @Override
    public void setValue(int i) {
        panel.setValue(i);
    }

    public void setLabelVisibility(boolean b) {
        panel.setLabelVisibility(b);
    }

    public void initProgress(String message, int i, int i0) {
        panel.initProgress(message, i0, i0);
    }

    public void setProgress(boolean indeterminant, boolean labelVisible, boolean paintProgressString, boolean visible, String message) {
        panel.setProgress(visible, indeterminant, visible, visible, message);
    }

    public ProgressDialogBase() {
        super();
        panels = new ArrayList<>();
        panel = new ProgressPanel();
        panels.add(panel);
        scrollPaneContainer = new JPanel();
        scrollPaneContainer.add(panel);
        scrollPaneContainer.setPreferredSize(panel.getSize());
        scrollPane = new JScrollPane(scrollPaneContainer);
  
        getContentPane().setLayout(new SpringLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        setSize(340, 110);
        setPreferredSize(new Dimension(340, 110));
        SpringUtilities.makeCompactGrid(getContentPane(),
                        1, 1, // rows, cols
                        6, 6, // initX, initY
                        10, 6); // xPad, yPad

        revalidate();
        repaint();
        pack();
        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
        setResizable(false);
        setAlwaysOnTop(true);
    }
}
