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
