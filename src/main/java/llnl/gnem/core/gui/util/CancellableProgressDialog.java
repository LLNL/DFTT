/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.core.gui.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

/**
 * User: dodge1 Date: May 27, 2005 Time: 1:38:38 PM
 */
@SuppressWarnings({"MagicNumber"})
public class CancellableProgressDialog extends ProgressDialogBase implements ActionListener {

    private static final long serialVersionUID = 8534456494181271284L;

    private final Collection<CancelListener> cancelListeners;
    private static final Map<Class, CancellableProgressDialog> namedInstances =  new ConcurrentHashMap<>();
    private final JButton cancelButton;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        for (CancelListener listener : cancelListeners) {
            listener.cancel();
        }
    }

    public void setPanelCount(int nPanels) {
        getContentPane().removeAll();
        getContentPane().setLayout(new SpringLayout());
        panels.clear();
        scrollPaneContainer.removeAll();
        scrollPane.removeAll();
       
        scrollPaneContainer = new JPanel();
        scrollPaneContainer.add(panel);
        panels.add(panel);
        
        int height = panel.getPreferredSize().height;
        int dlgWidth = panel.getPreferredSize().width;
      
        for (int j = 1; j < nPanels; ++j) {
            ProgressPanel p = new ProgressPanel();
            panels.add(p);
            scrollPaneContainer.add(p);
            height += p.getPreferredSize().height;
        }
        
        scrollPaneContainer.setPreferredSize(new Dimension(dlgWidth, height));
        scrollPane = new JScrollPane(scrollPaneContainer);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(cancelButton);
        height += (cancelButton.getPreferredSize().height + 20);
        setSize(dlgWidth + 80, height + 40);
        setPreferredSize(new Dimension(dlgWidth + 80, height + 40));
        SpringUtilities.makeCompactGrid(getContentPane(),
                //nPanels + 1, 1, //rows, cols
                2, 1, //rows, cols
                6, 6, //initX, initY
                10, 6);       //xPad, yPad
        
       revalidate();
       repaint();
       pack();
       setResizable(true);
       setAlwaysOnTop(true);
    }

    public ProgressPanel getPanel(int j) {
        return panels.get(j);
    }

    private static class ProgressDialogHolder {

        private static final CancellableProgressDialog instance = new CancellableProgressDialog();
    }

    public static CancellableProgressDialog getInstance() {
        return ProgressDialogHolder.instance;
    }

    public static CancellableProgressDialog getInstance(Class name) {
        CancellableProgressDialog result = namedInstances.get(name);
        if (result == null) {
            result = new CancellableProgressDialog();
            namedInstances.put(name, result);
        }
        return result;
    }

    private CancellableProgressDialog() {
        super();
        setResizable(true);
        cancelListeners = new ArrayList<>();
        panels = new ArrayList<>();
        getContentPane().removeAll();
        getContentPane().setLayout(new SpringLayout());
     
        scrollPaneContainer = new JPanel();
        scrollPaneContainer.add(panel);
      
        scrollPaneContainer.setPreferredSize(panel.getSize());
        scrollPane = new JScrollPane(scrollPaneContainer);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        ImageIcon icon = Utility.getIcon(this, "miscIcons/ignore16.gif");
        cancelButton = new JButton("Cancel", icon);
        cancelButton.addActionListener(this);
        cancelButton.setPreferredSize(new Dimension(60, 20));
        getContentPane().add(cancelButton);

        SpringUtilities.makeCompactGrid(getContentPane(),
                2, 1, //rows, cols
                6, 6, //initX, initY
                10, 6);       //xPad, yPad

        pack();
        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2,
                (dim.height - abounds.height) / 2);

        setResizable(false);
        setAlwaysOnTop(true);
        revalidate();
        repaint();
    }

    public void addCancelListener(CancelListener listener) {
        cancelListeners.add(listener);
    }

    public void clearCancelListeners() {
        cancelListeners.clear();
    }
}
