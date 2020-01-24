/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.util;

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
    
}
