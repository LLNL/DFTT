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
package llnl.gnem.apps.detection.dataAccess.interfaces;

import java.util.Collection;
import llnl.gnem.apps.detection.util.Configuration;
import llnl.gnem.core.dataAccess.DataAccessException;
import llnl.gnem.core.dataAccess.SeismogramSourceInfo;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public interface ConfigurationDAO {

    Collection<Configuration> getAllConfigurations() throws DataAccessException;

    void createOrReplaceConfigurationUsingInputFiles(String configName, String commandLineArgs, SeismogramSourceInfo sourceInfo) throws DataAccessException;

    void removeConfiguration(int configid) throws DataAccessException;

    int getConfigid(String configName) throws DataAccessException;

    String getConfigNameForRun(int runid) throws DataAccessException;

    boolean configExists(String configName) throws DataAccessException;

    void createConfiguration(String configName) throws DataAccessException;

    Collection<Integer> getStreamids(String configName) throws DataAccessException;

    Collection<StreamKey> getStreamKeys(int streamid) throws DataAccessException;

    SeismogramSourceInfo getConfigurationSeismogramSourceInfo(int configid) throws DataAccessException;
}
