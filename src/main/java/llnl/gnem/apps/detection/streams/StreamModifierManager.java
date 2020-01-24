/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.streams;

import java.util.ArrayList;

/**
 *
 * @author dodge
 */
public class StreamModifierManager {
    
    private final ArrayList<StreamModifier> modifiers;
    private StreamModifierManager() {
        modifiers = new ArrayList<>();
    }
    
    public static StreamModifierManager getInstance() {
        return StreamModifierManagerHolder.INSTANCE;
    }
    
    public ArrayList<StreamModifier> getModifiers()
    {
        return new ArrayList<>(modifiers);
    }

    public void addModifier(StreamModifier modifier) {
        modifiers.add(modifier);
    }
    
    private static class StreamModifierManagerHolder {

        private static final StreamModifierManager INSTANCE = new StreamModifierManager();
    }
}
