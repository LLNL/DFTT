/**
 * Created by: dodge1
 * Date: Dec 21, 2004
 *
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
package llnl.gnem.core.gui.plotting.keymapper;




public class KeyMapperManager {

    private static KeyMapperManager instance = null;
    private KeyMapperContainer container;
    private KeyMapperView view;

    public static KeyMapperManager getInstance()
    {
        if( instance == null )
            instance = new KeyMapperManager();
        return instance;
    }

    public void showGui()
    {
        container.setAlwaysOnTop( true );
        container.setVisible( true );
    }

    private KeyMapperManager()
    {
        container = KeyMapperContainer.getInstance();
        view = KeyMapperView.getInstance();
        view.update();
    }

}
