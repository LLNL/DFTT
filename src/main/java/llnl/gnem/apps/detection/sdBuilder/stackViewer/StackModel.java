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
package llnl.gnem.apps.detection.sdBuilder.stackViewer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.waveform.BaseTraceData;

/**
 *
 * @author dodge1
 */
public class StackModel {

    private final Map<StreamKey, StackData> elementMap;

    private StackModel() {
        elementMap = new HashMap<>();
    }

    public static StackModel getInstance() {
        return StackModelHolder.INSTANCE;
    }

    private static class StackModelHolder {

        private static final StackModel INSTANCE = new StackModel();
    }

    public Collection<StreamKey> getElementKeys() {
        return elementMap.keySet();
    }

    public StackData getStackData(StreamKey key) {
        return elementMap.get(key);
    }

    public void clear() {
        elementMap.clear();
    }

    public void setCurrentStack() {
        clear();
        Map<StreamKey, SingleComponentStack> keyStackMap = CorrelatedTracesModel.getInstance().getKeyStackMap();

        for (StreamKey key : keyStackMap.keySet()) {
            SingleComponentStack stack = keyStackMap.get(key);
            Double dNorth = stack.getDNorth();
            Double dEast = stack.getDeast();
            BaseTraceData data = stack.produceStack();
            elementMap.put(key, new StackData(key, stack, data, dNorth, dEast));
        }
    }

    public static final class StackData {

        private final StreamKey key;
        private final BaseTraceData stackTrace;
        private final Double dNorth;
        private final Double dEast;
        private final SingleComponentStack inputStackData;

        public StackData(StreamKey key, SingleComponentStack inputStackData, BaseTraceData stackTrace, Double dNorth,
                Double dEast) {
            this.key = key;
            this.inputStackData = inputStackData;
            this.stackTrace = stackTrace;
            this.dNorth = dNorth;
            this.dEast = dEast;
        }

        public StreamKey getKey() {
            return key;
        }

        public BaseTraceData getData() {
            return stackTrace;
        }

        public Double getdNorth() {
            return dNorth;
        }

        public Double getdEast() {
            return dEast;
        }

        public BaseTraceData getStackTrace() {
            return stackTrace;
        }

        public SingleComponentStack getInputStackData() {
            return inputStackData;
        }

    }
}
