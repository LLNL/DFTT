/**
 * Created by: dodge1
 * Date: Dec 21, 2004
 *
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
package llnl.gnem.core.gui.plotting.keymapper;

import llnl.gnem.core.gui.plotting.MouseMode;

import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.Preferences;


public class KeyMapperModel implements ControlKeyMapper {

    private static KeyMapperModel instance = null;

    private Vector<KeyMapperView> views;
    private HashMap<Integer, KeyMap> keyMap;
    private Vector<Integer> availableCodes;
    private Preferences prefs;
    private final static String PAN_MODE_STRING = "Pan-Mode Key";
    private final static String ZOOM_MODE_STRING = "Zoom-Only Key";
    private final static String CONTROL_SELECT_STRING = "Control-Select Key";
    private final static String REGION_SELECT_STRING = "Region-Select Key";
    private final static String DELETE_KEY_STRING = "Delete Key";

    public static KeyMapperModel getInstance()
    {
        if( instance == null )
            instance = new KeyMapperModel();
        return instance;
    }


    private KeyMapperModel()
    {
        views = new Vector<KeyMapperView>();
        keyMap = new HashMap<Integer, KeyMap>();
        availableCodes = new Vector<Integer>();
        availableCodes.add(KeyEvent.VK_SHIFT );
        availableCodes.add(KeyEvent.VK_ESCAPE );
        availableCodes.add(KeyEvent.VK_CONTROL );
        availableCodes.add(KeyEvent.VK_ALT );
        availableCodes.add(KeyEvent.VK_DELETE );
        availableCodes.add(KeyEvent.VK_F1 );
        availableCodes.add(KeyEvent.VK_F3 );
        availableCodes.add(KeyEvent.VK_F4 );
        availableCodes.add(KeyEvent.VK_F5 );
        availableCodes.add(KeyEvent.VK_F6 );
        availableCodes.add(KeyEvent.VK_F7 );
        availableCodes.add(KeyEvent.VK_F8 );
        availableCodes.add(KeyEvent.VK_F9 );
        availableCodes.add(KeyEvent.VK_F2 );
        availableCodes.add(KeyEvent.VK_F10 );
        availableCodes.add(KeyEvent.VK_F11 );
        availableCodes.add(KeyEvent.VK_F12 );

        prefs = Preferences.userRoot().node( "llnl/gnem/plotting/keymapper" );
        int key = prefs.getInt( PAN_MODE_STRING, KeyEvent.VK_SHIFT );
        availableCodes.remove( new Integer( key ) );
        keyMap.put(key, new KeyMap( key, PAN_MODE_STRING, MouseMode.PAN ) );

        key = prefs.getInt( ZOOM_MODE_STRING, KeyEvent.VK_ESCAPE );
        availableCodes.remove( new Integer( key ) );
        keyMap.put(key, new KeyMap( key, ZOOM_MODE_STRING, MouseMode.ZOOM_ONLY ) );

        key = prefs.getInt( CONTROL_SELECT_STRING, KeyEvent.VK_CONTROL );
        availableCodes.remove( new Integer( key ) );
        keyMap.put(key, new KeyMap( key, CONTROL_SELECT_STRING, MouseMode.CONTROL_SELECT ) );

        key = prefs.getInt( REGION_SELECT_STRING, KeyEvent.VK_ALT );
        availableCodes.remove( new Integer( key ) );
        keyMap.put(key, new KeyMap( key, REGION_SELECT_STRING, MouseMode.SELECT_REGION ) );

        key = prefs.getInt( DELETE_KEY_STRING, KeyEvent.VK_DELETE );
        availableCodes.remove( new Integer( key ) );
        keyMap.put(key, new KeyMap( key, DELETE_KEY_STRING, null ) );
    }

    public Vector<Integer> getAvailableCodes()
    {
        return availableCodes;
    }

    public Iterator<KeyMap> getKeyMapIterator()
    {
        return keyMap.values().iterator();
    }

    public void addView( KeyMapperView view )
    {
        views.add( view );
    }

    public void updateAllViews()
    {
        for (KeyMapperView view : views) {
            view.update();
        }
    }

    public void setKeyCode( String functionName, int code )
    {
        Collection<KeyMap> values = keyMap.values();
        for (KeyMap map : values) {
            if (map.getDescription().equals(functionName)) {
                Integer oldCode = map.getKeyCode();
                availableCodes.add(oldCode);
                availableCodes.remove((Integer)code);
                map.setKeyCode(code);
                keyMap.remove(oldCode);
                keyMap.put((Integer)code, map);
                prefs.putInt(functionName, code);
                updateAllViews();
                return;
            }
        }
    }


    public MouseMode getMouseMode( int keyCode )
    {
        KeyMap map = keyMap.get( new Integer( keyCode ) );
        if( map != null )
            return map.getMouseMode();
        else
            return null;
    }

    public boolean isDeleteKey( int keyCode )
    {
        KeyMap map = keyMap.get( new Integer( keyCode ) );
        if( map != null )
            return map.getDescription().equals( DELETE_KEY_STRING );
        else
            return false;
    }

    public boolean isControlKey( int keyCode )
    {
        KeyMap map = keyMap.get( keyCode );
        if( map != null )
            return map.getDescription().equals( CONTROL_SELECT_STRING );
        else
            return false;
    }

}
