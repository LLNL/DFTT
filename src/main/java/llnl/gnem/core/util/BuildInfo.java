/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.util;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 *
 * @author dodge1
 */
public class BuildInfo {

    private final String baseTitle;
    private final String buildInfoString;

    public BuildInfo(Class aclass) throws IOException {
        String className = aclass.getSimpleName() + ".class";
        String classPath = aclass.getResource(className).toString();
        if (classPath.startsWith("jar")) {
            String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
            Manifest mf = new Manifest(new URL(manifestPath).openStream());
            Attributes atts = mf.getMainAttributes();
            // Put this info in the log to help with analysis
            buildInfoString
                    = "Version:" + atts.getValue("Implementation-Version") + " Commit:"
                    + atts.getValue("Implementation-Build") + " Branch:" + atts.getValue("Build-Branch") + " By:"
                    + atts.getValue("Built-By") + " at " + atts.getValue("Build-Timestamp");
            // Update the title bar
            baseTitle = " Build(" + atts.getValue("Implementation-Build") + ") at " + atts.getValue("Build-Timestamp") + "  ";
        } else {
            // Class not from JAR
            buildInfoString = null;
            baseTitle = null;
        }
    }

    public boolean isFromJar() {
        return getBaseTitle() != null && getBuildInfoString() != null;
    }

    /**
     * @return the baseTitle
     */
    public String getBaseTitle() {
        return baseTitle;
    }

    /**
     * @return the buildInfoString
     */
    public String getBuildInfoString() {
        return buildInfoString;
    }
}
