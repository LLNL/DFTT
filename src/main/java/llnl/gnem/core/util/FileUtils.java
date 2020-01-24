package llnl.gnem.core.util;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * User: dodge1
 * Date: Dec 18, 2003
 * Time: 11:03:22 AM
 * To change this template use Options | File Templates.
 */
public class FileUtils {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    public final static String eps = "eps";

    /*
     * Get the extension of a file.
     */
    public static String getExtension( File f )
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf( '.' );

        if( i > 0 && i < s.length() - 1 ) {
            ext = s.substring( i + 1 ).toLowerCase();
        }
        return ext;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon( String path )
    {
        java.net.URL imgURL = FileUtils.class.getResource( path );
        if( imgURL != null ) {
            return new ImageIcon( imgURL );
        }
        else {
            System.err.println( "Couldn't find file: " + path );
            return null;
        }
    }

    public static boolean deleteDirectory(File path) throws IOException {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    if (!deleteDirectory(file)) {
                        throw new FileSystemException("Failed to delete directory: " + file);
                    }
                } else {
                    if (!file.delete()) {
                        throw new FileSystemException("Failed to delete file: " + file);
                    }
                }
            }
        }
        return (path.delete());

    }

}
