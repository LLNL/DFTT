package llnl.gnem.apps.detection.sdBuilder.arrayDisplay.actions;



import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.ArrayDisplayFrame;
import llnl.gnem.core.gui.util.Utility;


/**
 * Created by dodge1
 * Date: Mar 22, 2012
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class AutoScaleAction extends AbstractAction {
    private static AutoScaleAction ourInstance;

    public static AutoScaleAction getInstance(Object owner)
    {
        if( ourInstance == null )
            ourInstance = new AutoScaleAction(owner);
        return ourInstance;
    }

    private AutoScaleAction(Object owner)
    {
        super( "Auto-Scale", Utility.getIcon(owner,"miscIcons/fitInWindow32.gif") );
        putValue(SHORT_DESCRIPTION, "Apply default scale factors to all traces.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_A );
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        ArrayDisplayFrame.getInstance().autoScaleTraces();
    }

}