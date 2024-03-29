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
package llnl.gnem.apps.detection.util;

import java.io.File;
import llnl.gnem.dftt.core.util.FileUtil.DriveMapper;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author dodge1
 */

@ThreadSafe
public class Configuration {
    private final int configid;
    private final String name;
    private final String dir;
    private final String fileName;
    
    public Configuration(int configid,
        String name,
        String dir,
        String fileName)
        {
            this.configid = configid;
            this.name = name;
            this.dir = dir;
            this.fileName = fileName;
        }

    @Override
    public String toString()
    {
        return String.format("%d (%s)", configid, name);
    }
    
    /**
     * @return the configid
     */
    public int getConfigid() {
        return configid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the dir
     */
    public String getDir() {
        return dir;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }
    
    public String getAbsoluteFileName()
    {
        String tmpDir = DriveMapper.getInstance().maybeMapPath(dir);
        File file = new File(tmpDir,fileName);
        return file.getAbsolutePath();
    }
    
}
