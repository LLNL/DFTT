/*
 * ExceptionDialog.java
 *
 * Created on July 7, 2003, 3:25 PM
 */
package llnl.gnem.core.gui.util;


import javax.swing.*;

/**
 * @author ganz
 *         This class has several methods to display error/informational messages to a dialog box.
 *         You can send a database exception object, a string or a string array
 *         You may also optionally set the title.
 */
public class ExceptionDialog extends MessageDialog {
    // ERROR_MESSAGE = 0
    // PLAIN_MESSAGE = -1
    // INFORMATIONAL_MESSAGE = 1

    public ExceptionDialog()
    {
        //  this.setSize(600,200);
    }

    public static void displayError( Exception e )
    {
        ExceptionDialog smd = new ExceptionDialog();
        smd.SetMessageType( JOptionPane.WARNING_MESSAGE );
        smd.displayException( e );
        smd.dispose();
    }
}
