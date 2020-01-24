/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.core.gui.map.internal;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 * User: dodge1
 * Date: Dec 18, 2003
 * Time: 11:04:55 AM
 * To change this template use Options | File Templates.
 */
public class PngFilter extends FileFilter {
    private static final String defaultExtension = "PNG";

    @Override
    public boolean accept( File file )
    {
        if( file.isDirectory() ){
            return true;
        }

        String extension = FileUtils.getExtension( file );
        return extension != null && extension.equals(FileUtils.png);

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
        return "PNG Files";
    }


    public static File verifyFile(File chosen ){
        String parent = chosen.getParent();
        String name = chosen.getName();

        int dotpos = name.lastIndexOf(".");
        String filename = dotpos < 0 ? name + ".png" : name.substring(0, dotpos) + ".png";
        return new File(parent, filename);
    }


}
