/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.configuration;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import llnl.gnem.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge1
 */
public class MatchedFieldParamsPanel extends JPanel  {
    
    
    private final JFormattedTextField fftSizeField;
    private final JFormattedTextField matchedFieldDesignFactorField;
    private final JFormattedTextField matchedFieldDimensionField;
    private final JFormattedTextField staDurationField;
    private final JFormattedTextField ltaDurationField;
    private final JFormattedTextField gapDurationField;
  
   
    private final JCheckBox normalizeChk;
    private final JCheckBox prewhitenChk;
    private final JCheckBox spawningChk;
  
   
    
    public MatchedFieldParamsPanel(
            int fftSize,
            int matchedFieldDesignFactor,
            int matchedFieldDimension,
            double staDuration,
            double ltaDuration,
            double gapDuration,
            boolean normalizeStatistics,
            boolean prewhitenStatistics,
            boolean enableSpawning) {
        
        super(new SpringLayout());
        
       
        JLabel label = new JLabel("FFT Size", JLabel.TRAILING);
        add(label);
        fftSizeField = new JFormattedTextField(fftSize);
        fftSizeField.setColumns(10);
        add(fftSizeField);
        label.setLabelFor(fftSizeField);
        
        label = new JLabel("Matched Field DesignFactor", JLabel.TRAILING);
        add(label);
        matchedFieldDesignFactorField = new JFormattedTextField(matchedFieldDesignFactor);
        matchedFieldDesignFactorField.setColumns(10);
        add(matchedFieldDesignFactorField);
        label.setLabelFor(matchedFieldDesignFactorField);
        
        label = new JLabel("Matched Field Dimension", JLabel.TRAILING);
        add(label);
        matchedFieldDimensionField = new JFormattedTextField(matchedFieldDimension);
        matchedFieldDimensionField.setColumns(10);
        add(matchedFieldDimensionField);
        label.setLabelFor(matchedFieldDimensionField);
        
        label = new JLabel("STA Duration", JLabel.TRAILING);
        add(label);
        staDurationField = new JFormattedTextField(staDuration);
        staDurationField.setColumns(10);
        add(staDurationField);
        label.setLabelFor(staDurationField);
        
        label = new JLabel("LTA Duration", JLabel.TRAILING);
        add(label);
        ltaDurationField = new JFormattedTextField(ltaDuration);
        ltaDurationField.setColumns(10);
        add(ltaDurationField);
        label.setLabelFor(ltaDurationField);
        
        label = new JLabel("Gap Duration", JLabel.TRAILING);
        add(label);
        gapDurationField = new JFormattedTextField(gapDuration);
        gapDurationField.setColumns(10);
        add(gapDurationField);
        label.setLabelFor(gapDurationField);
        
        label = new JLabel("Normalize", JLabel.TRAILING);
        add(label);
        normalizeChk = new JCheckBox("",normalizeStatistics);
         label.setLabelFor(normalizeChk);
        add(normalizeChk);
        
        
        label = new JLabel("Pre-whiten", JLabel.TRAILING);
        add(label);
        prewhitenChk = new JCheckBox("", prewhitenStatistics);
        label.setLabelFor(prewhitenChk);
        add(prewhitenChk);
        
       
        label = new JLabel("Spawn Correlators", JLabel.TRAILING);
        add(label);
        spawningChk = new JCheckBox("", enableSpawning);
        label.setLabelFor(spawningChk);
        add(spawningChk); 
        
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 50));
        add(spacer);
        spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 50));
        add(spacer);
        this.setBorder(BorderFactory.createLineBorder(Color.blue));
        
        SpringUtilities.makeCompactGrid(this,
                10, 2,
                5, 5, //initX, initY
                5, 5);
    
    }
  
    
    public int getFFTSize() {
        return (Integer) fftSizeField.getValue();
    }
    
    public int getMatchedFieldDesignFactor() {
        return (Integer) matchedFieldDesignFactorField.getValue();
    }
    
    public int getMatchedFieldDimension() {
        return (Integer) matchedFieldDimensionField.getValue();
    }
    
    public double getStaDuration() {
        return (Double) staDurationField.getValue();
    }
    
    public double getLtaDuration() {
        return (Double) ltaDurationField.getValue();
    }
    
    public double getGapDuration() {
        return (Double) gapDurationField.getValue();
    }
    
    public boolean isNormalizeStatistics()
    {
        return normalizeChk.isSelected();
    }
    
    public boolean isPrewhitenStatistics()
    {
        return prewhitenChk.isSelected();
    }
    
    public boolean isSpawnOnTriggers()
    {
        return spawningChk.isSelected();
    }
 
}
