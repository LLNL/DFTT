package llnl.gnem.core.gui.util;

import javax.swing.*;

/**
 * User: Ganzberger1
 * Date: Jun 7, 2004
 * Time: 2:47:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileChooser extends JFrame {
    boolean DEBUG = false;

    public FileChooser()
    {
    }

    public String getFile( String title )
    {
        String fileName = "";

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory( new java.io.File( "." ) );
        //    chooser.setDialogTitle( "Select a File to import list of Evids." );
        chooser.setDialogTitle( title );
        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        int option = chooser.showOpenDialog( this );
        if( option == JFileChooser.APPROVE_OPTION ){
            if( chooser.getSelectedFile() != null ){
                fileName = chooser.getSelectedFile().getAbsolutePath();
                if( DEBUG ) System.out.println( "You picked the file : " + fileName );
            }
        }
        return fileName;
    }


}
