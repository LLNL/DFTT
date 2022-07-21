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
package llnl.gnem.apps.detection.dataAccess.database.javadb;

import llnl.gnem.apps.detection.dataAccess.interfaces.DetectionDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.DetectorDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.EventDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.OriginDAO;
import llnl.gnem.apps.detection.dataAccess.interfaces.PickDAO;

import llnl.gnem.apps.detection.dataAccess.interfaces.StationDAO;


/**
 *
 * @author dodge1
 */
public class DerbyFactoryHelper {


    public static EventDAO getEventDAO()
    {
        return DerbyEventDAO.getInstance();
    }

    public static OriginDAO getOriginDAO() {
        return DerbyOriginDAO.getInstance();
    }

    public static DetectionDAO getDetectionDAO() {
        return DerbyDetectionDAO.getInstance();
    }

    public static PickDAO getPickDAO() {
        return DerbyPickDAO.getInstance();
    }

    public static StationDAO getStationDAO() {
        return DerbyStationDAO.getInstance();
    }

    public static DetectorDAO getDetectorDAO() {
        return DerbyDetectorDAO.getInstance();
    }
    
}
