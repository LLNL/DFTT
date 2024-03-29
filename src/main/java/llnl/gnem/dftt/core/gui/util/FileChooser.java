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
package llnl.gnem.dftt.core.gui.util;

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
