package llnl.gnem.core.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * User: dodge1
 * Date: Dec 18, 2003
 * Time: 11:04:55 AM
 * To change this template use Options | File Templates.
 */
public class ImageFilter extends FileFilter {
    //Accept all directories and all eps files.
    public boolean accept( File f )
    {
        if( f.isDirectory() ){
            return true;
        }

        String extension = FileUtils.getExtension( f );
        if( extension != null ){
            if( extension.equals( FileUtils.eps ) ||
                    extension.equals( FileUtils.tif ) ||
                    extension.equals( FileUtils.gif ) ||
                    extension.equals( FileUtils.jpeg ) ||
                    extension.equals( FileUtils.jpg ) ||
                    extension.equals( FileUtils.png ) ){
                return true;
            }
            else{
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription()
    {
        return "Image Files";
    }

}
