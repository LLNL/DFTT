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
package llnl.gnem.apps.detection.util.initialization;

import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayDetectorSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTASpecification;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.apps.detection.core.framework.detectors.bulletin.BulletinSpecification;

/**
 * Created by dodge1
 * Date: Sep 30, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
public class BootDetectorParams {

    private final Map<String, Collection<STALTASpecification>> staLtaParamsMap;
    private final Map<String, Collection<ArrayDetectorSpecification>> arrayParamsMap;
    private final Map<String, Collection<BulletinSpecification>> bulletinParamsMap;

   
    public BootDetectorParams() {
        staLtaParamsMap = new HashMap<>();
        arrayParamsMap = new HashMap<>();
        bulletinParamsMap = new HashMap<>();
    }

    public void addStaLtaParams(String stream, STALTASpecification params) {
        Collection<STALTASpecification> tmp = staLtaParamsMap.get(stream);
        if (tmp == null) {
            tmp = new ArrayList<>();
            staLtaParamsMap.put(stream, tmp);
        }
        tmp.add(params);
    }

    public void addBulletinParams(String stream, BulletinSpecification params) {
        Collection<BulletinSpecification> tmp = bulletinParamsMap.get(stream);
        if (tmp == null) {
            tmp = new ArrayList<>();
            bulletinParamsMap.put(stream, tmp);
        }
        tmp.add(params);
    }

    public void addArrayParams(String stream, ArrayDetectorSpecification params) {
        Collection<ArrayDetectorSpecification> tmp = arrayParamsMap.get(stream);
        if (tmp == null) {
            tmp = new ArrayList<>();
            arrayParamsMap.put(stream, tmp);
        }
        tmp.add(params);
    }

    public Collection<STALTASpecification> getStaLtaParams(String stream) {
        Collection<STALTASpecification> result = new ArrayList<>();
        Collection<STALTASpecification> tmp = staLtaParamsMap.get(stream);
        if (tmp != null) {
            result.addAll(tmp);
        }
        return result;
    }

    public Collection<BulletinSpecification> getBulletinParams(String stream) {
        Collection<BulletinSpecification> result = new ArrayList<>();
        Collection<BulletinSpecification> tmp = bulletinParamsMap.get(stream);
        if (tmp != null) {
            result.addAll(tmp);
        }
        return result;
    }

    public Collection<ArrayDetectorSpecification> getArrayParams(String stream) {
        Collection<ArrayDetectorSpecification> result = new ArrayList<>();
        Collection<ArrayDetectorSpecification> tmp = arrayParamsMap.get(stream);
        if (tmp != null) {
            result.addAll(tmp);
        }
        return result;
    }
}
