/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dodge1
 */
public class KeyPhaseMapper {
    private final Map<Character,String> keyPhaseMap;
    private KeyPhaseMapper() {
        keyPhaseMap = new HashMap<>();
        keyPhaseMap.put('p', "P");
        keyPhaseMap.put('P', "P");
        keyPhaseMap.put('s', "S");
        keyPhaseMap.put('S', "S");
        
        keyPhaseMap.put('1', "Pn");
        keyPhaseMap.put('2', "Pg");
        keyPhaseMap.put('3', "Sn");
        keyPhaseMap.put('4', "Lg");
    }
    
    public static KeyPhaseMapper getInstance() {
        return KeyPhaseMapperHolder.INSTANCE;
    }
    
    private static class KeyPhaseMapperHolder {

        private static final KeyPhaseMapper INSTANCE = new KeyPhaseMapper();
    }
    
    public String getMappedPhase( char keyChar){
        return keyPhaseMap.get(keyChar);
    }
}
