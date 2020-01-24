/**
 * Created by: dodge1
 * Date: Dec 21, 2004
 *
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
package llnl.gnem.core.gui.plotting.keymapper;

import java.util.Iterator;


public class KeyMapperView {
    private static KeyMapperView instance = null;
    private KeyMapperModel model;
    private KeyMapperGui gui;

    public static KeyMapperView getInstance()
    {
        if( instance == null )
            instance = new KeyMapperView();
        return instance;
    }

    private KeyMapperView()
    {
        model = KeyMapperModel.getInstance();
        KeyMapperContainer container = KeyMapperContainer.getInstance();
        gui = new KeyMapperGui();
        Iterator<KeyMap> it = model.getKeyMapIterator();
        while( it.hasNext()){
            KeyMap map = it.next();
            gui.addLabeledCombo( new LabeledComboBox(map, model ) );
        }
        gui.layoutControls();
        container.addGuiComponents( gui );
        model.addView( this );

    }



    public void update()
    {
        gui.update( model.getAvailableCodes() );
        gui.repaint();
    }


}
