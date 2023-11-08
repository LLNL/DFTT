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
package llnl.gnem.dftt.core.gui.map.internal;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * User: dodge1
 * Date: Dec 18, 2003
 * Time: 11:04:55 AM
 * To change this template use Options | File Templates.
 */
public class GifFilter extends FileFilter {
    private static final String defaultExtension = "GIF";

    @Override
    public boolean accept( File file )
    {
        if( file.isDirectory() ){
            return true;
        }

        String extension = FileUtils.getExtension( file );
        return extension != null && extension.equals(FileUtils.gif);

    }

    /**
     *
     * @return
     */
    public static String getDefaultExtension() {
        return defaultExtension;
    }

    //The description of this filter
    @Override
    public String getDescription()
    {
        return "GIF Files";
    }


    public static File verifyFile(File chosen ){
        String parent = chosen.getParent();
        String name = chosen.getName();

        int dotpos = name.lastIndexOf(".");
        String filename = dotpos < 0 ? name + ".gif" : name.substring(0, dotpos) + ".gif";
        return new File(parent, filename);
    }

}
