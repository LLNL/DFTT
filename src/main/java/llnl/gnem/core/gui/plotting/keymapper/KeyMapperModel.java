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
package llnl.gnem.core.gui.plotting.keymapper;

import llnl.gnem.core.gui.plotting.MouseMode;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.prefs.Preferences;


public class KeyMapperModel implements ControlKeyMapper {

    private static KeyMapperModel instance = null;

    private final ArrayList<KeyMapperView> views;
    private final HashMap<Integer, KeyMap> keyMap;
    private final ArrayList<Integer> availableCodes;
    private final Preferences prefs;
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
        views = new ArrayList<>();
        keyMap = new HashMap<>();
        availableCodes = new ArrayList<>();
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

    public ArrayList<Integer> getAvailableCodes()
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


    @Override
    public MouseMode getMouseMode( KeyEvent e )
    {
        int keyCode = e.getKeyCode();
        KeyMap map = keyMap.get(keyCode);
        if( map != null )
            return map.getMouseMode();
        else
            return null;
    }

    @Override
    public boolean isDeleteKey( int keyCode )
    {
        KeyMap map = keyMap.get(keyCode);
        if( map != null )
            return map.getDescription().equals( DELETE_KEY_STRING );
        else
            return false;
    }

    @Override
    public boolean isControlKey( int keyCode )
    {
        KeyMap map = keyMap.get( keyCode );
        if( map != null )
            return map.getDescription().equals( CONTROL_SELECT_STRING );
        else
            return false;
    }

}
